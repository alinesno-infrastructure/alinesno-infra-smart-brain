package com.alinesno.infra.smart.assistant.scene.scene.longtext.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTemplateService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.ChapterGenFormDto;
import com.alinesno.infra.smart.scene.dto.ChatContentEditDto;
import com.alinesno.infra.smart.scene.dto.ChatRoleDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.*;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
public class LongTextTaskExecutionService {

    // 存储任务及其Future的映射
    private final ConcurrentMap<Long, Future<?>> taskFutures = new ConcurrentHashMap<>();

    // 存储章节任务及其Future的映射
    private final ConcurrentMap<Long, Future<?>> chapterTaskFutures = new ConcurrentHashMap<>();

//    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private ThreadPoolTaskExecutor executor;

    // 存储任务进度
    private final ConcurrentHashMap<Long, Integer> taskProgress = new ConcurrentHashMap<>();

    @Autowired
    private ISceneService service;

    @Autowired
    private ILongTextTaskService taskService;

    @Autowired
    private ILongTextTemplateService longTextTemplateService;

    @Autowired
    private ILongTextSceneService longTextSceneService;

    @Autowired
    private IChapterService chapterService;

    @Autowired
    private FormatMessageTool formatMessageTool;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer;

    @Autowired
    private IIndustryRoleService roleService;

    /**
     * 异步执行任务
     *
     * @param taskId 任务ID
     * @param channelStreamId 任务流ID
     * @param query  权限查询
     */
    public void executeTaskAsync(Long taskId, Long channelStreamId, PermissionQuery query) {
//        // 提交任务并存储Future
//        Future<?> future = executor.submit(() -> {
//            try {
//                internalExecuteTask(taskId, channelStreamId, query);
//            } catch (InterruptedException e) {
//                log.warn("任务被中断: taskId={}", taskId);
//                Thread.currentThread().interrupt(); // 恢复中断状态
//            }
//        });
//
//        taskFutures.put(taskId, future);

        CompletableFuture.runAsync(() -> {
            try {
                internalExecuteTask(taskId, channelStreamId, query);
            } catch (InterruptedException e) {
                log.warn("任务被中断: taskId={}", taskId);
                Thread.currentThread().interrupt();
            }
        }, executor)
        .exceptionally(ex -> {
            log.error("任务执行失败: taskId={}", taskId, ex);
            return null;
        });

    }

    /**
     * 内部执行任务方法（添加中断检查）
     */
    private void internalExecuteTask(Long taskId, Long channelStreamId, PermissionQuery query) throws InterruptedException {
        LongTextTaskEntity task = taskService.getById(taskId);
        try {
            if (task == null) return;

            // 检查中断状态
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("任务被取消");
            }

            LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(task.getSceneId(), query);
            ChatRoleDto dto = new ChatRoleDto();

            dto.setTaskId(taskId);
            dto.setChannelStreamId(String.valueOf(channelStreamId));
            dto.setSceneId(task.getSceneId());
            dto.setRoleId(Long.parseLong(longTextSceneEntity.getChapterEditor()));
            dto.setMessage(task.getTaskDescription() == null ? task.getTaskName() : task.getTaskDescription());

            List<TreeNodeDto> treeNodeDtos = chatRoleAsync(dto);

            chapterService.saveChaptersWithHierarchy(treeNodeDtos,
                    null,
                    1,
                    task.getSceneId(),
                    longTextSceneEntity.getId(),
                    query,
                    task);

            // 分发任务
            dispatchAgent(task.getSceneId(), taskId, query);

            // 再次检查中断状态
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("任务被取消");
            }

            // 任务完成
            task.setTaskStatus(String.valueOf(TaskStatusEnum.RUN_COMPLETED.getCode()));
            task.setOutline(JSONArray.toJSONString(treeNodeDtos));
            task.setTaskEndTime(new Date());
            taskService.updateById(task);

            // 执行章节任务
            executeChapterTask(taskId, query, task);

        } catch (Exception e) {
            log.error("任务执行失败", e);

            // 更新任务状态为失败
            task.setTaskStatus(e instanceof InterruptedException ?
                    String.valueOf(TaskStatusEnum.CANCELLED.getCode()) :
                    String.valueOf(TaskStatusEnum.RUN_FAILED.getCode()));
            task.setTaskEndTime(new Date());
            taskService.updateById(task);
        } finally {
            // 清理进度和Future
            taskProgress.remove(taskId);
            taskFutures.remove(taskId);
        }
    }

    /**
     * 执行章节任务
     */
    public void executeChapterTask(Long taskId, PermissionQuery query, LongTextTaskEntity task) {
        // 提交章节任务并存储Future
        Future<?> chapterFuture = executor.submit(() -> {
            try {
                internalExecuteChapterTask(taskId, query, task);
            } catch (InterruptedException e) {
                log.warn("章节任务被中断: taskId={}", taskId);
                Thread.currentThread().interrupt(); // 恢复中断状态
            }
        });

        chapterTaskFutures.put(taskId, chapterFuture);
    }

    /**
     * 内部执行章节任务方法（添加中断检查）
     */
    private void internalExecuteChapterTask(Long taskId, PermissionQuery query, LongTextTaskEntity task) throws InterruptedException {
        // 更新任务状态到章节生成中
        task.setChapterStatus(String.valueOf(TaskStatusEnum.RUNNING.getCode()));
        taskService.updateById(task);

        // 获取任务的所有章节
        List<ChapterEntity> chapters = chapterService.getChaptersByTaskId(taskId);
        int totalChapters = chapters.size();

        // 初始化进度
        taskProgress.put(taskId, 0);

        // 遍历所有章节并生成内容
        for (int i = 0; i < totalChapters; i++) {
            // 检查中断状态
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("章节任务被取消");
            }

            ChapterEntity chapter = chapters.get(i);

            // 构建章节生成DTO
            ChapterGenFormDto genFormDto = new ChapterGenFormDto();
            genFormDto.setChapterId(chapter.getId());
            genFormDto.setSceneId(task.getSceneId());
            genFormDto.setTaskId(taskId);
            genFormDto.setChapterTitle(chapter.getChapterName());
            genFormDto.setChapterDescription(chapter.getChapterRequire());
            genFormDto.setChannelStreamId(Long.valueOf(task.getChannelStreamId()));

            String processMsg = (i + 1) + ": 章节:" + genFormDto.getChapterTitle() + "任务生成中，还有【" + (chapters.size() - i) + "】篇";
            task.setCurrentChapterLabel(processMsg);

            // 调用章节生成逻辑
            try {
                String content = chatRoleSyncAsync(genFormDto, query).get();
                chapter.setContent(content);
                chapterService.update(chapter);
            } catch (InterruptedException e) {
                throw e; // 重新抛出中断异常
            } catch (Exception e) {
                log.error("章节生成失败", e);
            }

            // 更新进度
            int progress = (int) (((i + 1.0) / totalChapters) * 100);
            taskProgress.put(taskId, progress);

            task.setCurrentChapterId(chapter.getId());
            taskService.updateById(task);
        }

        // 最终检查中断状态
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("章节任务被取消");
        }

        task.setChapterStatus(String.valueOf(TaskStatusEnum.RUN_COMPLETED.getCode()));
        taskService.updateById(task);
    }

    /**
     * 生成大纲
     *
     * @param chatRole
     * @return
     */
    public List<TreeNodeDto> chatRoleAsync(ChatRoleDto chatRole) {
        log.info("开始处理chatRole异步请求，taskId: {}", chatRole.getTaskId());

        Long taskId = chatRole.getTaskId();
        LongTextTaskEntity longTextTaskEntity = taskService.getById(taskId);

        // 构建任务信息
        MessageTaskInfo taskInfo = chatRole.toPowerMessageTaskInfo();

        // 处理附件（如果有）
        if (StringUtil.isNotBlank(longTextTaskEntity.getAttachments())) {
            List<Long> attachmentIds = Arrays.stream(longTextTaskEntity.getAttachments().split(","))
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

        // 调用角色服务生成内容
        CompletableFuture<WorkflowExecutionDto> genContent = roleService.runRoleAgent(taskInfo);
        log.info("角色服务调用完成，taskId: {}", taskId);

//        // 获取模板信息
//        LongTextTemplateEntity template = longTextTemplateService.getById(longTextTaskEntity.getSelectedTemplateId());
//
//        // 格式化内容
//        formatMessageTool.handleChapterMessage(genContent, taskInfo, template);
//
//        // 处理代码内容（如果有）
//        if (genContent.getCodeContent() != null && !genContent.getCodeContent().isEmpty()) {
//            String codeContent = genContent.getCodeContent().get(0).getContent();
//
//            // 验证JSON格式
//            try {
//                JSONArray.parseArray(codeContent);
//                List<TreeNodeDto> nodeDtos = JSON.parseArray(codeContent, TreeNodeDto.class);
//                log.debug("解析成功，章节节点数: {}", nodeDtos.size());
//
//                return nodeDtos ;
//            } catch (Exception e) {
//                log.error("JSON解析失败", e);
//                throw new RpcServiceRuntimeException("生成大纲格式不正确，请点击重新生成");
//            }
//        }
        return Collections.emptyList() ;
    }

    /**
     * 异步生成章节内容
     */
    public CompletableFuture<String> chatRoleSyncAsync(ChapterGenFormDto dto, PermissionQuery query) {
        try {
            log.info("开始异步生成章节内容，chapterId: {}, sceneId: {}", dto.getChapterId(), dto.getSceneId());

            ChapterEntity chapterEntity = chapterService.getById(dto.getChapterId());
            if (chapterEntity == null) {
                return CompletableFuture.completedFuture("章节不存在");
            }

            Long roleId = chapterEntity.getChapterEditor();
            if (roleId == null) {
                return CompletableFuture.completedFuture("此章节未指定编辑人员");
            }

            LongTextTaskEntity longTextTaskEntity = taskService.getById(dto.getTaskId());

            // 准备任务信息
            MessageTaskInfo taskInfo = new MessageTaskInfo();
            taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
            taskInfo.setRoleId(roleId);
            taskInfo.setChannelId(dto.getSceneId());
            taskInfo.setSceneId(dto.getSceneId());
            taskInfo.setQueryText(dto.getChapterTitle() + ":" + dto.getChapterDescription());

            // 处理附件
            if (StringUtil.isNotBlank(longTextTaskEntity.getAttachments())) {
                List<Long> attachmentIds = Arrays.stream(longTextTaskEntity.getAttachments().split(","))
                        .map(Long::parseLong)
                        .toList();
                List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
                taskInfo.setAttachments(attachments);
            }

            // 准备章节内容
            String allChapterContent = chapterService.getAllChapterContent(dto.getSceneId());
            String chapterPromptContent = longTextTaskEntity.getTaskName();
            taskInfo.setText(FormatMessageTool.getChapterPrompt(
                    allChapterContent,
                    dto,
                    chapterPromptContent,
                    taskInfo
            ));

            // 执行角色生成
            CompletableFuture<WorkflowExecutionDto> genContent = roleService.runRoleAgent(taskInfo);
            log.info("章节内容生成完成，chapterId: {}", dto.getChapterId());

            // 更新章节内容
            chapterEntity.setContent(taskInfo.getFullContent());
            chapterService.update(chapterEntity);

            return CompletableFuture.completedFuture(chapterEntity.getContent());

        } catch (Exception e) {
            log.error("生成章节内容时发生异常", e);
            return CompletableFuture.completedFuture("生成失败: " + e.getMessage());
        }
    }

    /**
     * 分配智能助手到每个章节内容
     * @return
     */
    public void dispatchAgent(Long sceneId , Long taskId , PermissionQuery query) {

        SceneEntity entity = service.getById(sceneId) ;
        LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(sceneId , query) ;

        ChatContentEditDto dto = new ChatContentEditDto() ;
        dto.setSceneId(sceneId);
        dto.setRoleId(Long.parseLong(longTextSceneEntity.getContentEditor().split(",")[0]));

        LambdaQueryWrapper<ChapterEntity> lambdaQueryWrapper =  new LambdaQueryWrapper<ChapterEntity>()
                .eq(ChapterEntity::getSceneId, sceneId)
                .eq(ChapterEntity::getTaskId, taskId)
                .eq(ChapterEntity::getLongTextSceneId, longTextSceneEntity.getId());

        List<ChapterEntity> chapters = chapterService.list(lambdaQueryWrapper) ;
        List<Long> chapterIds = chapters.stream()
                .map(ChapterEntity::getId)
                .toList();

        List<String> chapterIdsStr = chapterIds.stream()
                .map(String::valueOf)
                .toList();

        dto.setChapters(chapterIdsStr);
        chapterService.updateChapterEditor(dto, longTextSceneEntity.getId() , taskId);
    }

    /**
     * 取消任务执行
     * @param taskId 任务ID
     * @return 是否取消成功
     */
    public boolean cancelTask(Long taskId) {
        boolean cancelled = false;

        // 1. 取消主任务
        Future<?> mainTaskFuture = taskFutures.get(taskId);
        if (mainTaskFuture != null) {
            cancelled = mainTaskFuture.cancel(true);
            taskFutures.remove(taskId);
            log.info("主任务取消结果: taskId={}, cancelled={}", taskId, cancelled);
        }

        // 2. 取消章节任务
        Future<?> chapterTaskFuture = chapterTaskFutures.get(taskId);
        if (chapterTaskFuture != null) {
            boolean chapterCancelled = chapterTaskFuture.cancel(true);
            chapterTaskFutures.remove(taskId);
            log.info("章节任务取消结果: taskId={}, cancelled={}", taskId, chapterCancelled);
            cancelled = cancelled || chapterCancelled;
        }

        // 3. 更新数据库状态
        if (cancelled) {
            LongTextTaskEntity task = taskService.getById(taskId);
            if (task != null) {
                task.setTaskStatus(String.valueOf(TaskStatusEnum.CANCELLED.getCode()));
                task.setChapterStatus(String.valueOf(TaskStatusEnum.CANCELLED.getCode()));
                task.setTaskEndTime(new Date());
                taskService.updateById(task);
            }
        }

        return cancelled;
    }

    /**
     * 获取任务进度
     *
     * @param taskId 任务ID
     * @return 进度百分比 (0-100)
     */
    public int getTaskProgress(Long taskId) {
        return taskProgress.getOrDefault(taskId, 0);
    }
}