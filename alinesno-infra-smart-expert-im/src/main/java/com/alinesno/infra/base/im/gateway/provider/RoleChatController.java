package com.alinesno.infra.base.im.gateway.provider;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.im.utils.MessageFormatter;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

//    @Autowired
//    private QianWenLLM qianWenLLM;

    @Autowired
    private ISSEService sseService;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

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

//        // 生成介绍的chat
//        Message userMsg = getMessage(role);
//        MessageManager msgManager = new MessageManager(10);
//        msgManager.add(userMsg);

        if(StringUtils.isNotBlank(role.getGreeting())){
            message.setChatText(role.getGreeting());
        }else{
//            StringBuilder stringBuilder = qianWenLLM.chatComponent(msgManager);
//            stringBuilder.toString());
            message.setChatText(role.getRoleName()) ;
        }
        message.setLoading(false);

        AjaxResult result = AjaxResult.success();

        result.put("role", role);
        result.put("message", message);

        return result;
    }

//    private static Message getMessage(IndustryRoleEntity role) {
//        String introBuilder = "你是一名[" +
//                role.getRoleName() +
//                "], " +
//                role.getRoleLevel() +
//                "，" +
//                role.getResponsibilities() +
//                "，现在有人咨询你，请你生成一句跟别人打招呼的语句，体现你的业务能力。";
//
//        return Message
//                .builder()
//                .role(Role.USER.getValue())
//                .content(introBuilder)
//                .build();
//    }

    /**
     * 角色对话
     *
     * @param chatMessage
     * @param roleId
     * @return
     */
    @SneakyThrows
    @PostMapping("/chatRole")
    public AjaxResult chatRole(@RequestBody ChatSendMessageDto chatMessage, long roleId) {

        IndustryRoleEntity role = industryRoleService.getById(roleId);

        long currentAccountId = CurrentAccountJwt.getUserId();

        List<IndustryRoleEntity> roleList = new ArrayList<>();
        roleList.add(role);

        List<ChatMessageDto> personDto = new ArrayList<>();
        roleList.forEach(r -> {
            ChatMessageDto msg = AgentUtils.getChatMessageDto(r, IdUtil.getSnowflakeNextId());
            msg.setAccountId(currentAccountId);
            personDto.add(msg);
        });

        chatMessage.setUsers(Collections.singletonList(roleId));
        chatMessage.setAccountId(currentAccountId);
        messageService.sendUserMessage(chatMessage, roleList, personDto);

        ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());

        msgDto.setChatText(MessageFormatter.getMessage(chatMessage.getMessage()));
        msgDto.setName(CurrentAccountJwt.get().getName());
        msgDto.setRoleType("person");
        msgDto.setIcon(CurrentAccountJwt.get().getAvatarPath()) ;
        msgDto.setFileAttributeList(storageConsumer.list(chatMessage.getFileIds()));

        return AjaxResult.success(msgDto);
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

        // 模拟返回文件的字数信息，这里假设你有获取字数的逻辑，这里简单返回0
//        int wordCount = 100;

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setId(r.getData());
        fileAttachment.setHasStatus(0);
        // 如果非图片类型，则解析出字数
//        if (!file.) {
//            fileAttachment.setWordCount(wordCount);
//        }

        return AjaxResult.success(fileAttachment) ;
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
