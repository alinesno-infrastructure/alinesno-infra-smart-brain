//package com.alinesno.infra.smart.assistant.scene.scene.longtext.tools;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alinesno.infra.common.facade.datascope.PermissionQuery;
//import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
//import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.IChapterService;
//import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextSceneService;
//import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTaskService;
//import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTemplateService;
//import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
//import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
//import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
//import com.alinesno.infra.smart.scene.dto.ChapterGenFormDto;
//import com.alinesno.infra.smart.scene.dto.ChatContentEditDto;
//import com.alinesno.infra.smart.scene.dto.ChatRoleDto;
//import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
//import com.alinesno.infra.smart.scene.entity.*;
//import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
//import com.alinesno.infra.smart.scene.service.ISceneService;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import jodd.util.StringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Service;
//
//import javax.lang.exception.RpcServiceRuntimeException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.*;
//
//@Slf4j
//@Service
//public class LongTextTaskExecutionServiceBak {
//
//    @Autowired
//    @Qualifier("chatThreadPool")
//    private ThreadPoolTaskExecutor chatThreadPool;
//
//    // 存储任务进度
//    private final ConcurrentHashMap<Long, Integer> taskProgress = new ConcurrentHashMap<>();
//
//    // 存储主任务的 Future
//    private final ConcurrentHashMap<Long, Future<?>> mainTaskFutures = new ConcurrentHashMap<>();
//
//    // 存储章节任务的 Future
//    private final ConcurrentHashMap<Long, Future<?>> chapterTaskFutures = new ConcurrentHashMap<>();
//
//    @Autowired
//    private ISceneService service;
//
//    @Autowired
//    private ILongTextTaskService taskService;
//
//    @Autowired
//    private ILongTextTemplateService longTextTemplateService;
//
//    @Autowired
//    private ILongTextSceneService longTextSceneService;
//
//    @Autowired
//    private IChapterService chapterService;
//
//    @Autowired
//    private FormatMessageTool formatMessageTool;
//
//    @Autowired
//    private CloudStorageConsumer cloudStorageConsumer;
//
//    @Autowired
//    private IIndustryRoleService roleService;
//
//    /**
//     * 异步执行任务（包含主任务和章节任务）
//     */
//    public void executeTaskAsync(Long taskId, Long channelStreamId, PermissionQuery query) {
//        // 提交主任务并保存 Future
//        CompletableFuture<LongTextTaskEntity> mainFuture = CompletableFuture
//                .supplyAsync(() -> {
//                    try {
//                        return internalExecuteTask(taskId, channelStreamId, query);
//                    } catch (Exception e) {
//                        throw new CompletionException(e);
//                    }
//                }, chatThreadPool);
//
//        mainTaskFutures.put(taskId, mainFuture);
//
//        CompletableFuture<Void> chapterFuture = mainFuture
//                .thenComposeAsync(taskEntity -> {
//                    log.info("大纲生成完成，开始执行章节任务，taskId={}", taskId);
//                    return internalExecuteChapterTask(taskId, query);
//                }, chatThreadPool)
//                .whenCompleteAsync((result, ex) -> {
//                    // 清理 Future 对象
//                    mainTaskFutures.remove(taskId);
//                    chapterTaskFutures.remove(taskId);
//
//                    // 更新任务的EndTime时间
//                    LongTextTaskEntity taskEntity = taskService.getById(taskId);
//                    taskEntity.setTaskEndTime(new Date());
//
//                    if (ex != null) {
//                        log.error("任务链执行失败", ex);
//                        taskEntity.setTaskStatus(TaskStatusEnum.RUN_FAILED.getCode());
//                    } else {
//                        log.info("任务链执行完成，taskId={}", taskId);
//                        taskEntity.setTaskStatus(TaskStatusEnum.RUN_COMPLETED.getCode());
//                    }
//
//                    taskService.update(taskEntity);
//                }, chatThreadPool);
//
//        chapterTaskFutures.put(taskId, chapterFuture);
//    }
//
//    /**
//     * 内部执行任务方法（生成大纲） - 现在直接返回实体而不是 CompletableFuture
//     */
//    private LongTextTaskEntity internalExecuteTask(Long taskId, Long channelStreamId, PermissionQuery query) {
//        // 检查是否被中断
//        if (Thread.interrupted()) {
//            log.info("任务已被取消，taskId={}", taskId);
//            throw new CancellationException("任务已被取消");
//        }
//
//        LongTextTaskEntity task = taskService.getById(taskId);
//        if (task == null) {
//            throw new IllegalArgumentException("任务不存在.");
//        }
//
//        try {
//
//            // 更新任务状态为"生成中"
//            task.setTaskStatus(TaskStatusEnum.RUNNING.getCode());
//            taskService.updateById(task);
//
//            LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(task.getSceneId(), query);
//            ChatRoleDto dto = new ChatRoleDto();
//
//            dto.setTaskId(taskId);
//            dto.setChannelStreamId(String.valueOf(channelStreamId));
//            dto.setSceneId(task.getSceneId());
//            dto.setRoleId(Long.parseLong(longTextSceneEntity.getChapterEditor()));
//            dto.setMessage(task.getTaskDescription() == null ? task.getTaskName() : task.getTaskDescription());
//
//            // 异步调用并等待结果
//            List<TreeNodeDto> treeNodeDtos = chatRoleAsync(dto).get();
//
//            chapterService.saveChaptersWithHierarchy(treeNodeDtos,
//                    null,
//                    1,
//                    task.getSceneId(),
//                    longTextSceneEntity.getId(),
//                    query,
//                    task);
//
//            // 分发任务
//            dispatchAgent(task.getSceneId(), taskId, query);
//
//            // 更新任务状态
//            task.setTaskStatus(TaskStatusEnum.RUN_COMPLETED.getCode());
//            task.setOutline(JSONArray.toJSONString(treeNodeDtos));
//            task.setTaskEndTime(new Date());
//            taskService.updateById(task);
//
//            return task;
//        } catch (Exception e) {
//            log.error("大纲生成失败", e);
//            // 更新任务状态为失败
//            task.setTaskStatus(TaskStatusEnum.RUN_FAILED.getCode());
//            task.setTaskEndTime(new Date());
//            taskService.updateById(task);
//
//            if (e instanceof CancellationException) {
//                Thread.currentThread().interrupt();
//                throw new CompletionException("任务被取消", e);
//            }
//            throw new CompletionException(e);
//        }
//    }
//
//    /**
//     * 内部执行章节任务方法（完全异步版本）
//     */
//    private CompletableFuture<Void> internalExecuteChapterTask(Long taskId, PermissionQuery query) {
//        LongTextTaskEntity task = taskService.getById(taskId);
//        if (task == null) {
//            return CompletableFuture.failedFuture(new IllegalArgumentException("任务不存在"));
//        }
//
//        // 更新任务状态到章节生成中
//        task.setChapterStatus(TaskStatusEnum.RUNNING.getCode());
//        taskService.updateById(task);
//
//        // 获取任务的所有章节
//        List<ChapterEntity> chapters = chapterService.getChaptersByTaskId(taskId);
//        int totalChapters = chapters.size();
//
//        // 初始化进度
//        taskProgress.put(taskId, 0);
//
//        // 创建顺序执行的CompletableFuture链
//        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
//
//        for (int i = 0; i < totalChapters; i++) {
//            final int index = i;
//            final ChapterEntity chapter = chapters.get(i);
//            chain = chain.thenComposeAsync(ignored -> {
//                // 检查是否被中断
//                if (Thread.interrupted()) {
//                    log.info("章节任务已被取消，taskId={}, chapterId={}", taskId, chapter.getId());
//                    throw new CancellationException("任务被取消");
//                }
//
//                // 构建章节生成DTO
//                ChapterGenFormDto genFormDto = new ChapterGenFormDto();
//                genFormDto.setChapterId(chapter.getId());
//                genFormDto.setSceneId(task.getSceneId());
//                genFormDto.setTaskId(taskId);
//                genFormDto.setChapterTitle(chapter.getChapterName());
//                genFormDto.setChapterDescription(chapter.getChapterRequire());
//                genFormDto.setChannelStreamId(Long.valueOf(task.getChannelStreamId()));
//
//                String processMsg = (index + 1) + ": 章节:" + genFormDto.getChapterTitle() + "任务生成中，还有【" + (chapters.size() - index) + "】篇";
//                task.setCurrentChapterLabel(processMsg);
//
//                // 异步执行章节生成
//                return chatRoleSyncAsync(genFormDto, query)
//                        .thenApplyAsync(content -> {
//                            // 再次检查是否被中断
//                            if (Thread.interrupted()) {
//                                log.info("章节内容保存被取消，taskId={}, chapterId={}", taskId, chapter.getId());
//                                throw new CancellationException("任务被取消");
//                            }
//                            return content;
//                        }, chatThreadPool)
//                        .thenAcceptAsync(content -> {
//                            chapter.setContent(content);
//                            chapterService.update(chapter);
//
//                            // 更新进度
//                            int progress = (int) (((index + 1.0) / totalChapters) * 100);
//                            taskProgress.put(taskId, progress);
//
//                            task.setCurrentChapterId(chapter.getId());
//                            taskService.updateById(task);
//                        }, chatThreadPool)
//                        .exceptionally(e -> {
//                            // 捕获所有异常，记录错误但继续执行后续章节
//                            log.error("章节生成失败但继续后续任务，chapterId={}, 错误: {}",  chapter.getId(), e.getMessage());
//                            return null;
//                        });
//            }, chatThreadPool);
//        }
//
//        return chain
//                .thenRunAsync(() -> {
//                    task.setChapterStatus(TaskStatusEnum.RUN_COMPLETED.getCode());
//                    taskService.updateById(task);
//                }, chatThreadPool)
//                .exceptionally(ex -> {
//                    if (ex instanceof CancellationException) {
//                        task.setChapterStatus(TaskStatusEnum.CANCELLED.getCode());
//                    } else {
//                        task.setChapterStatus(TaskStatusEnum.RUN_FAILED.getCode());
//                    }
//                    taskService.updateById(task);
//                    return null;
//                });
//    }
//
//    /**
//     * 取消任务
//     */
//    public boolean cancelTask(Long taskId) {
//        // 1. 获取任务实体
//        LongTextTaskEntity task = taskService.getById(taskId);
//        if (task == null) {
//            return false;
//        }
//
//        // 2. 尝试取消主任务
//        boolean mainCancelled = cancelFuture(taskId, mainTaskFutures, "主任务");
//
//        // 3. 尝试取消章节任务
//        boolean chapterCancelled = cancelFuture(taskId, chapterTaskFutures, "章节任务");
//
//        // 4. 更新任务状态
//        task.setTaskStatus(TaskStatusEnum.CANCELLED.getCode());
//        task.setChapterStatus(TaskStatusEnum.CANCELLED.getCode());
//        task.setTaskEndTime(new Date());
//        taskService.updateById(task);
//
//        log.info("任务取消完成，taskId={}, 主任务取消={}, 章节任务取消={}",
//                taskId, mainCancelled, chapterCancelled);
//
//        return mainCancelled || chapterCancelled;
//    }
//
//    private boolean cancelFuture(Long taskId, ConcurrentMap<Long, Future<?>> futureMap, String taskType) {
//        Future<?> future = futureMap.remove(taskId);
//        if (future != null) {
//            boolean cancelled = future.cancel(true); // true 表示中断正在执行的任务
//            log.info("{}取消结果: taskId={}, cancelled={}", taskType, taskId, cancelled);
//            return cancelled;
//        }
//        return false;
//    }
//
//    /**
//     * 获取任务进度
//     */
//    public int getTaskProgress(Long taskId) {
//        return taskProgress.getOrDefault(taskId, 0);
//    }
//
//    /**
//     * 分配智能助手到每个章节内容
//     * @return
//     */
//    public void dispatchAgent(Long sceneId , Long taskId , PermissionQuery query) {
//
//        SceneEntity entity = service.getById(sceneId) ;
//        LongTextSceneEntity longTextSceneEntity = longTextSceneService.getBySceneId(sceneId , query) ;
//
//        ChatContentEditDto dto = new ChatContentEditDto() ;
//        dto.setSceneId(sceneId);
//        dto.setRoleId(Long.parseLong(longTextSceneEntity.getContentEditor().split(",")[0]));
//
//        LambdaQueryWrapper<ChapterEntity> lambdaQueryWrapper =  new LambdaQueryWrapper<ChapterEntity>()
//                .eq(ChapterEntity::getSceneId, sceneId)
//                .eq(ChapterEntity::getTaskId, taskId)
//                .eq(ChapterEntity::getLongTextSceneId, longTextSceneEntity.getId());
//
//        List<ChapterEntity> chapters = chapterService.list(lambdaQueryWrapper) ;
//        List<Long> chapterIds = chapters.stream()
//                .map(ChapterEntity::getId)
//                .toList();
//
//        List<String> chapterIdsStr = chapterIds.stream()
//                .map(String::valueOf)
//                .toList();
//
//        dto.setChapters(chapterIdsStr);
//        chapterService.updateChapterEditor(dto, longTextSceneEntity.getId() , taskId);
//    }
//
//    /**
//     * 智能助手生成章节内容
//     * @param dto
//     * @param query
//     * @return
//     */
//    public CompletableFuture<String> chatRoleSyncAsync(ChapterGenFormDto dto, PermissionQuery query) {
//        try {
//            log.info("开始异步生成章节内容，chapterId: {}, sceneId: {}", dto.getChapterId(), dto.getSceneId());
//
//            ChapterEntity chapterEntity = chapterService.getById(dto.getChapterId());
//            if (chapterEntity == null) {
//                return CompletableFuture.completedFuture("章节不存在");
//            }
//
//            Long roleId = chapterEntity.getChapterEditor();
//            if (roleId == null) {
//                return CompletableFuture.completedFuture("此章节未指定编辑人员");
//            }
//
//            LongTextTaskEntity longTextTaskEntity = taskService.getById(dto.getTaskId());
//
//            // 准备任务信息
//            MessageTaskInfo taskInfo = new MessageTaskInfo();
//            taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
//            taskInfo.setRoleId(roleId);
//            taskInfo.setChannelId(dto.getSceneId());
//            taskInfo.setSceneId(dto.getSceneId());
//            taskInfo.setQueryText(dto.getChapterTitle() + ":" + dto.getChapterDescription());
//
//            // 处理附件
//            if (StringUtil.isNotBlank(longTextTaskEntity.getAttachments())) {
//                List<Long> attachmentIds = Arrays.stream(longTextTaskEntity.getAttachments().split(","))
//                        .map(Long::parseLong)
//                        .toList();
//                List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
//                taskInfo.setAttachments(attachments);
//            }
//
//            // 准备章节内容
//            String allChapterContent = chapterService.getAllChapterContent(dto.getSceneId());
//            String chapterPromptContent = longTextTaskEntity.getTaskName();
//            taskInfo.setText(FormatMessageTool.getChapterPrompt(
//                    allChapterContent,
//                    dto,
//                    chapterPromptContent,
//                    taskInfo
//            ));
//
//            // 修改为完全异步的方式
//            return roleService.runRoleAgent(taskInfo)
//                    .thenApply(genContent -> {
//                        log.info("章节内容生成完成，chapterId: {}", dto.getChapterId());
//                        chapterEntity.setContent(taskInfo.getFullContent());
//                        chapterService.update(chapterEntity);
//                        return chapterEntity.getContent();
//                    })
//                    .exceptionally(ex -> {
//                        log.error("生成章节内容时发生异常", ex);
//                        return "生成失败: " + ex.getMessage();
//                    });
//
//        } catch (Exception e) {
//            log.error("准备章节内容时发生异常", e);
//            return CompletableFuture.completedFuture("生成失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 生成大纲 - 异步版本
//     */
//    public CompletableFuture<List<TreeNodeDto>> chatRoleAsync(ChatRoleDto chatRole) {
//        log.info("开始处理chatRole异步请求，taskId: {}", chatRole.getTaskId());
//
//        Long taskId = chatRole.getTaskId();
//        LongTextTaskEntity longTextTaskEntity = taskService.getById(taskId);
//
//        try {
//            // 构建任务信息
//            MessageTaskInfo taskInfo = chatRole.toPowerMessageTaskInfo();
//
//            // 处理附件（如果有）
//            if (StringUtil.isNotBlank(longTextTaskEntity.getAttachments())) {
//                List<Long> attachmentIds = Arrays.stream(longTextTaskEntity.getAttachments().split(","))
//                        .map(Long::parseLong)
//                        .toList();
//                List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
//                taskInfo.setAttachments(attachments);
//            }
//
//            taskInfo.setRoleId(chatRole.getRoleId());
//            taskInfo.setChannelStreamId(chatRole.getChannelStreamId());
//            taskInfo.setChannelId(chatRole.getSceneId());
//            taskInfo.setSceneId(chatRole.getSceneId());
//            taskInfo.setText(chatRole.getMessage());
//            taskInfo.setQueryText(chatRole.getMessage());
//
//            // 获取模板信息
//            LongTextTemplateEntity template = longTextTemplateService.getById(longTextTaskEntity.getSelectedTemplateId());
//
//            // 完全异步处理
//            return roleService.runRoleAgent(taskInfo)
//                    .thenApply(genContent -> {
//                        log.info("角色服务调用完成，taskId: {}", taskId);
//
//                        // 格式化内容
//                        formatMessageTool.handleChapterMessage(genContent, taskInfo, template);
//
//                        // 处理代码内容（如果有）
//                        if (genContent.getCodeContent() != null && !genContent.getCodeContent().isEmpty()) {
//                            String codeContent = genContent.getCodeContent().get(0).getContent();
//
//                            try {
//                                JSONArray.parseArray(codeContent); // 验证JSON格式
//                                List<TreeNodeDto> nodeDtos = JSON.parseArray(codeContent, TreeNodeDto.class);
//                                log.debug("解析成功，章节节点数: {}", nodeDtos.size());
//                                return nodeDtos;
//                            } catch (Exception e) {
//                                log.error("JSON解析失败", e);
//                                throw new RpcServiceRuntimeException("生成大纲格式不正确，请点击重新生成");
//                            }
//                        }
//                        return Collections.<TreeNodeDto>emptyList();
//                    });
//        } catch (Exception e) {
//            log.error("处理chatRole异步请求时发生异常", e);
//            return CompletableFuture.failedFuture(e);
//        }
//    }
//
//}