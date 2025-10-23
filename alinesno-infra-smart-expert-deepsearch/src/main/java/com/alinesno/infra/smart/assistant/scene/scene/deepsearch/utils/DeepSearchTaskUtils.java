package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.web.adapter.base.dto.ManagerAccountDto;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.scene.dto.DeepsearchChatRoleDto;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.alinesno.infra.smart.utils.AgentUtils;
import com.alinesno.infra.smart.utils.MessageFormatter;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DeepSearch任务工具类
 */
@Slf4j
@Component
public class DeepSearchTaskUtils {

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IIndustryRoleService industryRoleService;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private IDeepSearchTaskService taskService ;

    @Autowired
    private ThreadPoolTaskExecutor chatThreadPool ;

    @Autowired
    private IIndustryRoleService roleService;

    public void executeTaskAsync(DeepsearchChatRoleDto chatRole, PermissionQuery query, String sessionId) {

        // 新起一个线程来执行任务
        log.debug("开始异步执行任务：{}", chatRole.getMessage());

        try {
            // 构建任务信息
            MessageTaskInfo taskInfo = chatRole.toPowerMessageTaskInfo();
            taskInfo.setSessionId(sessionId); // 绑定会话ID
            Long taskId = chatRole.getTaskId();

            // 处理附件（如果有）
            if (StringUtil.isNotBlank(chatRole.getAttachments())) {
                List<Long> attachmentIds = Arrays.stream(chatRole.getAttachments().split(","))
                        .map(Long::parseLong)
                        .toList();
                List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
                taskInfo.setAttachments(attachments);
            }

            taskInfo.setRoleId(chatRole.getRoleId());
            taskInfo.setChannelStreamId(chatRole.getChannelStreamId());
            taskInfo.setChannelId(chatRole.getSceneId());
            taskInfo.setSceneId(chatRole.getSceneId());
            taskInfo.setText(chatRole.getMessage());
            taskInfo.setQueryText(chatRole.getMessage());
            taskInfo.setSceneTaskId(taskId);

            // 完全异步处理，添加异常处理
            roleService.runRoleAgent(taskInfo)
                    .thenApply(genContent -> {
                        log.info("角色服务调用完成（会话ID：{}），taskId: {}", sessionId, taskId);

                        // 更新任务状态
                        DeepSearchTaskEntity task = taskService.getById(taskId);
                        task.setTaskStatus(String.valueOf(TaskStatusEnum.RUN_COMPLETED.getCode()));
                        task.setTaskEndTime(new Date());
                        taskService.updateById(task);

                        return null;
                    })
                    .exceptionally(throwable -> {
                        // 处理异步执行过程中的异常
                        log.error("异步任务执行失败（会话ID：{}），taskId: {}", sessionId, taskId, throwable);

                        // 更新任务状态为失败
                        try {
                            DeepSearchTaskEntity task = taskService.getById(taskId);
                            task.setTaskStatus(String.valueOf(TaskStatusEnum.RUN_FAILED.getCode()));
                            task.setTaskEndTime(new Date());
                            task.setErrorMessage(throwable.getMessage());
                            taskService.updateById(task);
                        } catch (Exception e) {
                            log.error("更新任务状态失败（会话ID：{}），taskId: {}", sessionId, taskId, e);
                        }

                        return null;
                    });

        } catch (Exception e) {
            // 处理同步代码块的异常
            log.error("异步任务初始化失败（会话ID：{}），taskId: {}", sessionId, chatRole.getTaskId(), e);

            // 更新任务状态为失败
            try {
                Long taskId = chatRole.getTaskId();
                DeepSearchTaskEntity task = taskService.getById(taskId);
                task.setTaskStatus(String.valueOf(TaskStatusEnum.RUN_FAILED.getCode()));
                task.setTaskEndTime(new Date());
                task.setErrorMessage(e.getMessage());
                taskService.updateById(task);
            } catch (Exception ex) {
                log.error("更新任务状态失败（会话ID：{}），taskId: {}", sessionId, chatRole.getTaskId(), ex);
            }
        }
    }

    /**
     * 生成获取聊天消息
     * @param chatRole
     * @param managerAccountDto
     * @return
     */
    public ChatMessageDto getChatMessageDto(DeepsearchChatRoleDto chatRole, ManagerAccountDto managerAccountDto) {

        String name = managerAccountDto.getName();
        String avatarPath = managerAccountDto.getAvatarPath();
        Long roleId = chatRole.getRoleId();

        IndustryRoleEntity role = industryRoleService.getById(roleId);
        ChatMessageDto msgDto = AgentUtils.getChatMessageDto(role, IdUtil.getSnowflakeNextId());
        msgDto.setChatText(MessageFormatter.getMessage(chatRole.getMessage()));
        msgDto.setName(name); // 使用预先获取的名称
        msgDto.setRoleType("person");
        msgDto.setIcon(avatarPath); // 使用预先获取的头像路径
        msgDto.setSessionId(chatRole.getSessionId()); // 消息绑定会话ID

        if(chatRole.getAttachments() != null) {
            List<Long> attachmentIds = Arrays.stream(chatRole.getAttachments().split(","))
                    .map(Long::parseLong)
                    .toList();
            msgDto.setFileAttributeList(storageConsumer.list(attachmentIds));
        }

        return msgDto;
    }

    /**
     * 合并附件
     * @param chatAttachments
     * @param taskAttachments
     * @return
     */
    public String mergeAttachments(String chatAttachments, String taskAttachments) {
        // 将两个附件字符串转为 Stream，处理 null 和空白
        Stream<String> chatStream = parseToStream(chatAttachments);
        Stream<String> taskStream = parseToStream(taskAttachments);

        // 合并流、过滤空元素（可选去重）、拼接
        return Stream.concat(chatStream, taskStream)
                .filter(s -> !s.trim().isEmpty()) // 过滤拆分后可能的空字符串
                 .distinct() // 如需去重，解开注释
                .collect(Collectors.joining(","));
    }

    // 辅助方法：将字符串转为 Stream（处理 null 和空白）
    private Stream<String> parseToStream(String str) {
        if (str == null || str.trim().isEmpty()) {
            return Stream.empty(); // 空字符串/null 直接返回空流
        }
        return Arrays.stream(str.split(",")); // 非空则拆分转为流
    }

}
