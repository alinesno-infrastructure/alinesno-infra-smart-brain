package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.base.im.utils.MessageFormatter;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.RoleQuestionSuggestionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 使用于多个网站针对于专家单独咨询的接口
 */
@Slf4j
@Scope("prototype")
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/roleChat/")
public class RoleChatController extends SuperController {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Autowired
    private ISSEService sseService;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private ThreadPoolTaskExecutor chatThreadPool;

    /**
     * 获取角色信息
     *
     * @param roleId
     * @return
     */
    @SneakyThrows
    @GetMapping("/getInfo")
    public AjaxResult getInfo(long roleId) {

        IndustryRoleEntity role = industryRoleService.getById(roleId);
        ChatMessageDto message = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());

        if(StringUtils.isNotBlank(role.getGreeting())){
            message.setChatText(role.getGreeting());
        }else{
            message.setChatText(role.getRoleName()) ;
        }

        // 设置用户问题
        if(StringUtils.isNotBlank(role.getGreetingQuestion())){
            List<String> greetingQuestion = JSONArray.parseArray(role.getGreetingQuestion() , String.class) ;
            message.setGreetingQuestion(greetingQuestion);
        }

        message.setLoading(false);

        AjaxResult result = AjaxResult.success();

        result.put("role", role);
        result.put("message", message);

        return result;
    }

    /**
     * 角色对话
     *
     * @param chatMessage
     * @param roleId
     * @return
     */
//    @SneakyThrows
//    @PostMapping("/chatRole")
//    public AjaxResult chatRole(@RequestBody ChatSendMessageDto chatMessage, long roleId) {
//
//        IndustryRoleEntity role = industryRoleService.getById(roleId);
//
//        long currentAccountId = CurrentAccountJwt.getUserId();
//
//        List<IndustryRoleEntity> roleList = new ArrayList<>();
//        roleList.add(role);
//
//        List<ChatMessageDto> personDto = new ArrayList<>();
//        roleList.forEach(r -> {
//            ChatMessageDto msg = AgentUtils.getChatMessageDto(r, IdUtil.getSnowflakeNextId());
//            msg.setAccountId(currentAccountId);
//            personDto.add(msg);
//        });
//
//        chatMessage.setUsers(Collections.singletonList(roleId));
//        chatMessage.setAccountId(currentAccountId);
//        chatMessage.setAccountOrgId(CurrentAccountJwt.get().getOrgId());
//        messageService.sendUserMessage(chatMessage, roleList, personDto);
//
//        ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());
//
//        msgDto.setChatText(MessageFormatter.getMessage(chatMessage.getMessage()));
//        msgDto.setName(CurrentAccountJwt.get().getName());
//        msgDto.setRoleType("person");
//        msgDto.setIcon(CurrentAccountJwt.get().getAvatarPath()) ;
//        msgDto.setFileAttributeList(storageConsumer.list(chatMessage.getFileIds()));
//
//        return AjaxResult.success(msgDto);
//    }

    @SneakyThrows
    @PostMapping("/chatRole")
    public DeferredResult<AjaxResult> chatRole(@RequestBody ChatSendMessageDto chatMessage, long roleId) {
        DeferredResult<AjaxResult> deferredResult = new DeferredResult<>();

        // 在主线程（Web上下文存在时）先获取所有需要的信息
        long currentAccountId = CurrentAccountJwt.getUserId();
        long accountOrgId = CurrentAccountJwt.get().getOrgId();
        String name = CurrentAccountJwt.get().getName();
        String avatarPath = CurrentAccountJwt.get().getAvatarPath();

        CompletableFuture.supplyAsync(() -> {
            try {
                IndustryRoleEntity role = industryRoleService.getById(roleId);
                List<IndustryRoleEntity> roleList = new ArrayList<>();
                roleList.add(role);

                List<ChatMessageDto> personDto = new ArrayList<>();
                roleList.forEach(r -> {
                    ChatMessageDto msg = AgentUtils.getChatMessageDto(r, IdUtil.getSnowflakeNextId());
                    msg.setAccountId(currentAccountId); // 使用预先获取的ID
                    personDto.add(msg);
                });

                chatMessage.setUsers(Collections.singletonList(roleId));
                chatMessage.setAccountId(currentAccountId);
                chatMessage.setAccountOrgId(accountOrgId); // 使用预先获取的orgId
                messageService.sendUserMessage(chatMessage, roleList, personDto);

                ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());
                msgDto.setChatText(MessageFormatter.getMessage(chatMessage.getMessage()));
                msgDto.setName(name); // 使用预先获取的名称
                msgDto.setRoleType("person");
                msgDto.setIcon(avatarPath); // 使用预先获取的头像路径
                msgDto.setFileAttributeList(storageConsumer.list(chatMessage.getFileIds()));

                return AjaxResult.success(msgDto);
            } catch (Exception e) {
                log.error("角色对话处理失败", e);
                return AjaxResult.error("角色对话处理失败: " + e.getMessage());
            }
        }, chatThreadPool).whenComplete((result, ex) -> {
            if (ex != null) {
                deferredResult.setErrorResult(AjaxResult.error("处理请求时发生异常"));
            } else {
                deferredResult.setResult(result);
            }
        });

        return deferredResult;
    }

    // 处理文件上传的方法
    @SneakyThrows
    @PostMapping("/uploadFile")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file) {

        // 文件保存在系统临时目录
        if (file.isEmpty()) {
            return AjaxResult.error("文件为空");
        }

        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = tempDir.resolve(fileName);

        File targetFile =  new File(String.valueOf(filePath)) ;
        file.transferTo(targetFile);

        R<String> r = storageConsumer.upload(targetFile) ;

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setId(r.getData());
        fileAttachment.setHasStatus(0);

        return AjaxResult.success(fileAttachment) ;
    }

    /**
     * 获取用户提问问题建议
     */
    @PostMapping("/getRoleQuestionSuggestion")
    public AjaxResult getQuestionSuggestion(@RequestBody RoleQuestionSuggestionDto dto) {
        log.debug("获取用户提问问题建议:{}" , dto);

        List<String> questionSug =  industryRoleService.getQuestionSuggestion(dto) ;

        return AjaxResult.success("操作成功." , questionSug);
    }


    /**
     * 文件信息
     */
    @Data
    static class FileAttachment {
        private String id ;  // 文件id
        private int wordCount ; // 文件字数
        private int hasStatus ; // 上传中|解析中|入库中
    }
}
