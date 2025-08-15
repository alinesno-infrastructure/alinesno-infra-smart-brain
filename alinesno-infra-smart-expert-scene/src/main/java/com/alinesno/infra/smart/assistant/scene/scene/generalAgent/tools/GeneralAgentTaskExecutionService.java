package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.prompt.GeneralAgentPrompt;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTemplateService;
import com.alinesno.infra.smart.scene.dto.*;
import com.alinesno.infra.smart.scene.entity.*;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentPlanService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.tools.FormatMessageTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jodd.util.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GeneralAgentTaskExecutionService {

    @Autowired
    private ThreadPoolTaskExecutor executor;

    // 存储任务进度
    private final ConcurrentHashMap<Long, Integer> taskProgress = new ConcurrentHashMap<>();

    @Autowired
    private ISceneService service;

    @Autowired
    private IGeneralAgentTaskService taskService;

    @Autowired
    private IGeneralAgentTemplateService generalAgentTemplateService;

    @Autowired
    private IGeneralAgentSceneService generalAgentSceneService;

    @Autowired
    private GeneralAgentFormatMessageTool formatMessageTool;

    @Autowired
    private IGeneralAgentPlanService generalAgentPlanService;

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
        GeneralAgentTaskEntity task = taskService.getById(taskId);

        try {
            if (task == null) return;

            task.setGenPlanStatus(String.valueOf(TaskStatusEnum.RUNNING.getCode()));
            taskService.updateById(task);

            // 检查中断状态
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("任务被取消");
            }

            GeneralAgentSceneEntity longTextSceneEntity = generalAgentSceneService.getBySceneId(task.getSceneId(), query);
            ChatRoleDto dto = new ChatRoleDto();

            dto.setTaskId(taskId);
            dto.setChannelStreamId(String.valueOf(channelStreamId));
            dto.setSceneId(task.getSceneId());
            dto.setRoleId(Long.parseLong(longTextSceneEntity.getBusinessProcessorEngineer()));
            dto.setMessage(task.getTaskDescription() == null ? task.getTaskName() : task.getTaskDescription());

            List<TreeNodeDto> treeNodeDtos = chatRoleAsync(dto);
            generalAgentPlanService.saveChaptersWithHierarchy(treeNodeDtos, null,
                    1 ,
                    task.getSceneId() ,
                    taskId ,
                    longTextSceneEntity.getId());

            // 分发任务
            dispatchAgent(task.getSceneId(), taskId, query);

            // 任务完成
            task.setGenPlanStatus(String.valueOf(TaskStatusEnum.RUN_COMPLETED.getCode()));
            task.setOutline(JSONArray.toJSONString(treeNodeDtos));
            task.setTaskEndTime(new Date());
            taskService.updateById(task);

            // 执行章节任务
            executeChapterTask(taskId, query, task);

            // TODO 执行生成内容结果
            // executeResultTask(taskId, query) ;

        } catch (Exception e) {
            log.error("任务执行失败", e);

            // 更新任务状态为失败
            task.setGenPlanStatus(e instanceof InterruptedException ?
                    String.valueOf(TaskStatusEnum.CANCELLED.getCode()) :
                    String.valueOf(TaskStatusEnum.RUN_FAILED.getCode()));
            task.setTaskEndTime(new Date());
            taskService.updateById(task);
        } finally {
            // 清理进度和Future
            taskProgress.remove(taskId);
        }
    }

    /**
     * 生成内容结果
     * @param taskId
     * @param query
     */
    private void executeResultTask(Long taskId, PermissionQuery query) {
        GeneralAgentTaskEntity task = taskService.getById(taskId);
        task.setGenResultStatus(String.valueOf(TaskStatusEnum.RUNNING.getCode()));
        taskService.updateById(task);

        GeneralAgentSceneEntity longTextSceneEntity = generalAgentSceneService.getBySceneId(task.getSceneId(), query);

        // 准备任务信息
        MessageTaskInfo taskInfo = new MessageTaskInfo();
        taskInfo.setChannelStreamId(String.valueOf(task.getChannelStreamId()));
        taskInfo.setRoleId(Long.parseLong(longTextSceneEntity.getDataViewerEngineer()));
        taskInfo.setChannelId(task.getSceneId());
        taskInfo.setSceneId(task.getSceneId());
        taskInfo.setQueryText(task.getTaskPrompt()) ;

        // 处理附件
        if (StringUtil.isNotBlank(task.getAttachments())) {
            List<Long> attachmentIds = Arrays.stream(task.getAttachments().split(","))
                    .map(Long::parseLong)
                    .toList();
            List<FileAttachmentDto> attachments = cloudStorageConsumer.list(attachmentIds);
            taskInfo.setAttachments(attachments);
        }

        // 获取模板信息
        GeneralAgentTemplateEntity template = generalAgentTemplateService.getById(task.getSelectedTemplateId());
        log.debug("获取模板信息完成，template.Name: {}", template.getName());

        taskInfo.setText(GeneralAgentPrompt.getResultPrompt(
            taskInfo,
            template
        ));

        task.setGenResultStatus(String.valueOf(TaskStatusEnum.RUN_COMPLETED.getCode()));
        taskService.updateById(task);
    }

    /**
     * 执行章节任务
     */
    public void executeChapterTask(Long taskId, PermissionQuery query, GeneralAgentTaskEntity task) {

        // 更新任务状态到章节生成中
        task.setGentContentStatus(String.valueOf(TaskStatusEnum.RUNNING.getCode()));
        taskService.updateById(task);

        // 获取任务的所有章节
        List<GeneralAgentPlanEntity> chapters = generalAgentPlanService.getChaptersByTaskId(taskId);
        int totalChapters = chapters.size();

        // 初始化进度
        taskProgress.put(taskId, 0);

        // 遍历所有章节并生成内容
        for (int i = 0; i < totalChapters; i++) {

            GeneralAgentPlanEntity chapter = chapters.get(i);

            // 构建章节生成DTO
            GeneralAgentChapterGenFormDto genFormDto = new GeneralAgentChapterGenFormDto();
            genFormDto.setChapterId(chapter.getId());
            genFormDto.setSceneId(task.getSceneId());
            genFormDto.setTaskId(taskId);
            genFormDto.setChapterTitle(chapter.getPlanName());
            genFormDto.setChapterDescription(chapter.getPlanRequire());
            genFormDto.setChannelStreamId(Long.valueOf(task.getChannelStreamId()));

            String processMsg = (i + 1) + ": 章节:" + genFormDto.getChapterTitle() + "任务生成中，还有【" + (chapters.size() - i) + "】篇";
            task.setCurrentChapterLabel(processMsg);

            // 调用章节生成逻辑
            try {
                String content = chatRoleSyncAsync(genFormDto, query).get();
                chapter.setContent(content);
                generalAgentPlanService.update(chapter);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            } catch (Exception e) {
                log.error("章节生成失败", e);
            }

            // 更新进度
            int progress = (int) (((i + 1.0) / totalChapters) * 100);
            taskProgress.put(taskId, progress);

            task.setCurrentChapterId(chapter.getId());
            taskService.updateById(task);
        }

        task.setGentContentStatus(String.valueOf(TaskStatusEnum.RUN_COMPLETED.getCode()));
        taskService.updateById(task);
    }

    /**
     * 生成大纲
     *
     * @param chatRole
     * @return
     */
    @SneakyThrows
    public List<TreeNodeDto> chatRoleAsync(ChatRoleDto chatRole) {
        log.info("开始处理chatRole异步请求，taskId: {}", chatRole.getTaskId());

        Long taskId = chatRole.getTaskId();
        GeneralAgentTaskEntity generalAgentTaskEntity = taskService.getById(taskId);

        // 构建任务信息
        MessageTaskInfo taskInfo = chatRole.toPowerMessageTaskInfo();

        // 处理附件（如果有）
        if (StringUtil.isNotBlank(generalAgentTaskEntity.getAttachments())) {
            List<Long> attachmentIds = Arrays.stream(generalAgentTaskEntity.getAttachments().split(","))
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
        CompletableFuture<WorkflowExecutionDto> futureGenContent = roleService.runRoleAgent(taskInfo);
        log.info("角色服务调用完成，taskId: {}", taskId);

        WorkflowExecutionDto genContent = futureGenContent.get();

        // 获取模板信息
        GeneralAgentTemplateEntity template = generalAgentTemplateService.getById(generalAgentTaskEntity.getSelectedTemplateId());
        log.debug("获取模板信息完成，template.Name: {}", template.getName());

        // 格式化内容
        formatMessageTool.handleChapterMessage(genContent, taskInfo);

        // 处理代码内容（如果有）
        if (genContent.getCodeContent() != null && !genContent.getCodeContent().isEmpty()) {
            String codeContent = genContent.getCodeContent().get(0).getContent();

            // 验证JSON格式
            try {
                JSONArray.parseArray(codeContent);
                List<TreeNodeDto> nodeDtos = JSON.parseArray(codeContent, TreeNodeDto.class);
                log.debug("解析成功，章节节点数: {}", nodeDtos.size());

                return nodeDtos ;
            } catch (Exception e) {
                log.error("JSON解析失败", e);
                throw new RpcServiceRuntimeException("生成大纲格式不正确，请点击重新生成");
            }
        }
        return Collections.emptyList() ;
    }

    /**
     * 异步生成章节内容
     */
    public CompletableFuture<String> chatRoleSyncAsync(GeneralAgentChapterGenFormDto dto, PermissionQuery query) {
        try {
            log.info("开始异步生成章节内容，chapterId: {}, sceneId: {}", dto.getChapterId(), dto.getSceneId());

            GeneralAgentPlanEntity chapterEntity = generalAgentPlanService.getById(dto.getChapterId());
            if (chapterEntity == null) {
                return CompletableFuture.completedFuture("章节不存在");
            }

            Long roleId = chapterEntity.getBusinessExecutorEngineerId() ; //.getChapterEditor();
            if (roleId == null) {
                return CompletableFuture.completedFuture("此章节未指定编辑人员");
            }

            GeneralAgentTaskEntity longTextTaskEntity = taskService.getById(dto.getTaskId());

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
            String allChapterContent = generalAgentPlanService.getAllChapterContent(dto.getSceneId());
            String chapterPromptContent = longTextTaskEntity.getTaskName();

            ChapterGenFormDto chapterGenFormDto  = new ChapterGenFormDto();
            BeanUtils.copyProperties(dto, chapterGenFormDto) ;

            taskInfo.setText(FormatMessageTool.getChapterPrompt(
                    allChapterContent,
                    chapterGenFormDto,
                    chapterPromptContent,
                    taskInfo
            ));

            // 执行角色生成
            CompletableFuture<WorkflowExecutionDto> futureGenContent = roleService.runRoleAgent(taskInfo);
            log.info("章节内容生成完成，chapterId: {}", dto.getChapterId());

            futureGenContent.get() ;

            // 更新章节内容
            chapterEntity.setContent(taskInfo.getFullContent());
            generalAgentPlanService.update(chapterEntity);

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
        GeneralAgentSceneEntity longTextSceneEntity = generalAgentSceneService.getBySceneId(sceneId , query) ;

        ChatContentEditDto dto = new ChatContentEditDto() ;
        dto.setSceneId(sceneId);
        dto.setRoleId(Long.parseLong(longTextSceneEntity.getBusinessExecuteEngineer().split(",")[0]));

        LambdaQueryWrapper<GeneralAgentPlanEntity> lambdaQueryWrapper =  new LambdaQueryWrapper<GeneralAgentPlanEntity>()
                .eq(GeneralAgentPlanEntity::getSceneId, sceneId)
                .eq(GeneralAgentPlanEntity::getTaskId, taskId)
                .eq(GeneralAgentPlanEntity::getGeneralAgentSceneId, longTextSceneEntity.getId());

        List<GeneralAgentPlanEntity> chapters = generalAgentPlanService.list(lambdaQueryWrapper) ;
        List<Long> chapterIds = chapters.stream()
                .map(GeneralAgentPlanEntity::getId)
                .toList();

        List<String> chapterIdsStr = chapterIds.stream()
                .map(String::valueOf)
                .toList();

        dto.setChapters(chapterIdsStr);
        generalAgentPlanService.updateChapterEditor(dto, longTextSceneEntity.getId() , taskId);
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