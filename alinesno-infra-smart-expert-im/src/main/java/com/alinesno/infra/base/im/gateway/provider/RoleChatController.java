package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alinesno.infra.base.im.service.IRoleChatService;
import com.alinesno.infra.base.im.utils.AgentUtils;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
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

import java.util.List;
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
        ChatMessageDto message = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId()) ;

        // 生成介绍的chat
        Message userMsg = getMessage(role);

        MessageManager msgManager = new MessageManager(10);
        msgManager.add(userMsg);

        StringBuilder stringBuilder = qianWenLLM.chatComponent(msgManager) ;
        message.setChatText(stringBuilder.toString());
        message.setLoading(false);

        AjaxResult result = AjaxResult.success() ;

        result.put("role" , role);
        result.put("message" , message) ;

        return result ;
    }

    private static Message getMessage(IndustryRoleEntity role) {
        String introBuilder = "你是一名[" +
                role.getRoleName() +
                "], " +
                role.getRoleLevel() +
                "，" +
                role.getResponsibilities() +
                "，现在有人咨询你，请你生成一句跟别人打招呼的语句，体现你的业务能力。";

        return Message
                .builder()
                .role(Role.USER.getValue())
                .content(introBuilder)
                .build();
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

        ThreadUtil.execute(() -> {
            try {
                runChatStream(role , message , emitter);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        long bId = IdUtil.getSnowflakeNextId() ;
        ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, bId) ;

        msgDto.setChatText(message.getContent());
        msgDto.setName("软件工程师罗小东");
        msgDto.setRoleType("person");
        msgDto.setIcon("1808350003370057729");

        return AjaxResult.success(msgDto);
    }

    private void runChatStream(IndustryRoleEntity role, PromptMessageDto message, SseEmitter emitter) throws InterruptedException {

        IBaseExpertService expertService = getBaseExpertService(role);
        List<PromptMessage> parseMessageList = expertService.promptMessages(message.getContent() , role) ;

        MessageManager msgManager = new MessageManager(10);

        for(PromptMessage m : parseMessageList) {
            com.alibaba.dashscope.common.Message msg = com.alibaba.dashscope.common.Message.builder()
                    .role(m.getRole())
                    .content(m.getContent())
                    .build();
            msgManager.add(msg);
        }

        Message userMsg = Message
                .builder()
                .role(Role.USER.getValue())
                .content(message.getContent())
                .build();

        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();
        long bId = IdUtil.getSnowflakeNextId() ;

        qianWenLLM.getGeneration(msgManager , new ResultCallback<>() {
            @SneakyThrows
            @Override
            public void onEvent(GenerationResult message) {

                String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
                String finishReason = message.getOutput().getChoices().get(0).getFinishReason() ;

                log.info("Received message: {}", JsonUtils.toJson(message));

                ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, bId) ;
                msgDto.setLoading(false);
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

    /**
     * 根据行业角色获取专家服务
     *
     * @param role 行业角色实体对象，包含了链ID等信息，用于定位特定的专家服务实例
     * @return 返回对应的专家服务接口实现对象如果无法获取到合适的实现对象，将返回默认角色的专家服务
     */
    private IBaseExpertService getBaseExpertService(IndustryRoleEntity role) {
        IBaseExpertService expertService = null;

        // 从Spring上下文中根据链ID获取Bean
        try{
            expertService = (IBaseExpertService) SpringContext.getBean(role.getChainId());
        }catch (Exception e){
            log.error("无法获取到指定的专家服务实例，使用默认角色", e);
            expertService = (IBaseExpertService) SpringContext.getBean(AssistantConstants.PREFIX_ASSISTANT_DEFAULT); // 使用默认角色
        }

        return expertService;
    }

}
