package com.alinesno.infra.smart.assistant.publish.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.constants.PublishConst;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.publish.service.IChannelPublishService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 渠道聊天控制器，用于控制渠道聊天的请求和响应，包括流量限制等处理。
 */
@Slf4j
@RestController
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/publishChat")
public class PublishChatController {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IIndustryRoleService industryRoleService;

//    @Autowired
//    private QianWenLLM qianWenLLM;

    @Autowired
    private IChannelPublishService channelPublishService ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    /**
     * 语音播放 speechRecognition
     */
    @SneakyThrows
    @PostMapping(value = "/playShareGenContent")
    public ResponseEntity<Resource> playShareGenContent(@RequestBody ChatMessageDto dto, @RequestParam String shareId) {
        log.debug("语音播放请求已收到 , shareId = {}" , shareId);

        IndustryRoleEntity role = industryRoleService.getById(dto.getRoleId()) ;
        String filePath = llmModelService.speechSynthesisFile(role , dto) ;

        log.debug("getSpeech filePath = {}" , filePath);

        try {
            // 本地录音文件路径
            File audioFile = new File(filePath) ;

            // 确保文件存在
            if (!audioFile.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            FileInputStream fileInputStream = new FileInputStream(audioFile);
            InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(audioFile.length());
            headers.setContentDispositionFormData("attachment", audioFile.getName());

            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取角色信息
     *
     * @param shareId
     * @return
     */
    @SneakyThrows
    @GetMapping("/getShareInfo")
    public AjaxResult getShareInfo(@RequestParam String shareId) {

        long roleId = channelPublishService.getByShareToken(shareId).getRoleId();

        IndustryRoleEntity role = industryRoleService.getById(roleId);
        ChatMessageDto message = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());

        // 生成介绍的chat
//        Message userMsg = getMessage(role);
//        MessageManager msgManager = new MessageManager(10);
//        msgManager.add(userMsg);

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

    // 新增的接收FormData数据的方法
    // 新增的接收FormData数据的方法
    @SneakyThrows
    @PostMapping(value = "/shareRecognize", consumes = "multipart/form-data")
    public AjaxResult recognizeFormData(
            @RequestPart("act") String act,
            @RequestPart("prompt") String prompt,
            @RequestPart("duration") Double duration,
            @RequestPart("file") MultipartFile file
    ) {
        log.debug("语音识别（FormData）请求已收到");

        // 获取系统临时目录
        String tempDir = System.getProperty("java.io.tmpdir");

        // 保存文件
        if (!file.isEmpty()) {
            File dest = new File(tempDir + File.separator + file.getOriginalFilename());

            log.debug("文件保存路径: " + dest.getAbsolutePath());

            file.transferTo(dest);
        }

        // 打印接收到的信息
        log.debug("act: " + act);
        log.debug("prompt: " + prompt);
        log.debug("duration: " + duration);
        log.debug("文件名: " + file.getOriginalFilename());

        return AjaxResult.success("语音识别成功");
    }

//    private static Message getMessage(IndustryRoleEntity role) {
//        String introBuilder = "你是一名[" + role.getRoleName() +
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
     * @param shareId
     * @return
     */
    @SneakyThrows
    @PostMapping("/chatShareRole")
    public AjaxResult chatShareRole(@RequestBody ChatSendMessageDto chatMessage, String shareId) {

        long roleId = channelPublishService.getByShareToken(shareId).getRoleId();

        IndustryRoleEntity role = industryRoleService.getById(roleId);

        long currentAccountId = PublishConst.ANONYMOUS_USER ; // CurrentAccountJwt.getUserId();

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

        msgDto.setChatText(chatMessage.getMessage());
        msgDto.setName(PublishConst.ANONYMOUS_USERNAME);
        msgDto.setRoleType("person");
        msgDto.setIcon(PublishConst.ANONYMOUS_ICON) ;
        msgDto.setFileAttributeList(storageConsumer.list(chatMessage.getFileIds()));

        return AjaxResult.success(msgDto);
    }
}