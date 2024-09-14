package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alinesno.infra.base.im.service.IRoleChatService;
import com.alinesno.infra.base.im.utils.AgentUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.role.llm.QianWenLLM;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.service.ISSEService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Semaphore;

/**
 * 使用于多个网站针对于专家单独咨询的接口
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/roleChat/")
public class RoleChatController extends SuperController {

    @Autowired
    private IRoleChatService roleChatService;

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Autowired
    private QianWenLLM qianWenLLM;

    @Autowired
    private ISSEService sseService ;

    /**
     * 获取角色信息
     * @param roleId
     * @return
     */
    @SneakyThrows
    @GetMapping("/getInfo")
    public AjaxResult getInfo(long roleId){
        IndustryRoleEntity role = industryRoleService.getById(roleId) ;

        ChatMessageDto message = AgentUtils.getChatMessageDto(role, 0) ;
        // sseService.send(String.valueOf(roleId), message);

        return AjaxResult.success(role) ;
    }

    /**
     * 角色对话
     * @param message
     * @return
     */
    @SneakyThrows
    @PostMapping("/chatRole")
    public AjaxResult chatRole(@RequestBody PromptMessageDto message , long roleId){

        IndustryRoleEntity role = industryRoleService.getById(roleId) ;
        SseEmitter emitter = sseService.getConn(String.valueOf(role.getId())) ;

        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runChatStream(role , emitter);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return AjaxResult.success();
    }

    private void runChatStream(IndustryRoleEntity role, SseEmitter emitter) throws InterruptedException {

        Message userMsg = Message
                .builder()
                .role(Role.USER.getValue())
                .content("如何做西红柿炖牛腩？")
                .build();

        MessageManager msgManager = new MessageManager(10);
        msgManager.add(userMsg);

        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();

        qianWenLLM.getGeneration(msgManager , new ResultCallback<>() {
            @SneakyThrows
            @Override
            public void onEvent(GenerationResult message) {

                String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
                String finishReason = message.getOutput().getChoices().get(0).getFinishReason() ;

                log.info("Received message: {}", JsonUtils.toJson(message));

                ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, 0) ;
                msgDto.setChatText(msg);
                emitter.send(msgDto) ;

                if (finishReason != null && finishReason.equals("stop")) {
                    emitter.send("[DONE]");
                    semaphore.release();
                }
            }

            @Override
            public void onError(Exception err) {
                log.error("Exception occurred: {}", err.getMessage());
                semaphore.release();
            }

            @Override
            public void onComplete() {
                log.info("Completed");
                semaphore.release();
                log.info("Full content: \n{}", fullContent);
            }
        }) ;

        semaphore.acquire();
    }

}
