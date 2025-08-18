package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ExecuteTaskDto;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.prompt.ProjectPromptHandle;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectResearchSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;
import com.alinesno.infra.smart.scene.entity.ProjectResearchSceneEntity;
import com.alinesno.infra.smart.scene.entity.ProjectTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectTaskExecutorService {

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private IProjectTaskService projectTaskService;

    @Autowired
    private IProjectKnowledgeGroupService projectKnowledgeGroupService;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer;

    @Autowired
    private IProjectResearchSceneService projectResearchSceneService;

    @Autowired
    private ProjectContentSummaryTool projectContentSummaryTool ;

    @Autowired
    private IAccountPointService accountPointService ;

    @Autowired
    @Qualifier("chatThreadPool")
    private ThreadPoolTaskExecutor chatThreadPool;

    public void executeTaskAsync(ExecuteTaskDto dto, PermissionQuery query, ProjectTaskEntity taskEntity) {
        chatThreadPool.execute(() -> {

            long userId = query.getOperatorId() ;
            long orgId = query.getOrgId() ;
            long taskId = taskEntity.getId() ;

            // 启动任务
            accountPointService.startSceneTask(userId, orgId , taskId);

            ProjectResearchSceneEntity sceneEntity = projectResearchSceneService.getBySceneId(dto.getSceneId(), query);
            Long progressAnalyzerEngineer = sceneEntity.getProgressAnalyzerEngineer();

            try {
                taskEntity.setTaskStatus(TaskStatusEnum.RUNNING.getCode());
                taskEntity.setFailureReason(null);
                projectTaskService.updateById(taskEntity);

                StringBuilder detailedContent = new StringBuilder();
                List<Section> flatSections = getSectionList(taskEntity);
                String currentStepLabel = "" ;

                int count = 0;
                int total = flatSections.size();

                for (Section section : flatSections) {
                    String label = section.label;
                    String description = section.description;
                    count ++ ;

                    currentStepLabel = "当前任务执行章节:" + label + "("+count+ "/" + total + ")" ;  ;
                    updateTaskStepLabelEntity(taskEntity, currentStepLabel);

                    MessageTaskInfo taskInfo = createTaskInfo(query, taskEntity, label, description, progressAnalyzerEngineer, dto);
                    CompletableFuture<WorkflowExecutionDto> genContent = roleService.runRoleAgent(taskInfo);
                    genContent.get() ; // 阻塞当前线程

                    // 构建详细内容
                    if (section.isMain) {
                        detailedContent.append("## ").append(section.label).append("\n");
                    } else {
                        detailedContent.append("### ").append(section.label).append("\n");
                    }
                    detailedContent.append(taskInfo.getFullContent() == null ? "" : taskInfo.getFullContent()).append("\n\n");
                }

                currentStepLabel = "当前任务执行章节: 总结" ;
                updateTaskStepLabelEntity(taskEntity, currentStepLabel);
                MessageTaskInfo taskInfo = createSummaryTaskInfo(query, taskEntity, progressAnalyzerEngineer, dto);

                // 获取到总结内容
                String summaryContent = projectContentSummaryTool.handleChapterMessage(taskEntity.getDetailedContent() , taskEntity.getOutline() , taskInfo) ;
                taskEntity.setSummarizedContent(summaryContent);

                // 保存结果
                taskEntity.setDetailedContent(detailedContent.toString());
                taskEntity.setTaskStatus(TaskStatusEnum.RUN_COMPLETED.getCode());
                taskEntity.setTaskEndTime(new Date());
                projectTaskService.updateById(taskEntity);

            } catch (Exception e) {
                log.error("Task execution failed", e);
                taskEntity.setTaskStatus(TaskStatusEnum.RUN_FAILED.getCode());
                taskEntity.setFailureReason(getFailureReason(e));
                taskEntity.setTaskEndTime(new Date());
                projectTaskService.updateById(taskEntity);
            }

            // 结束任务
            accountPointService.endSceneTask(userId, orgId , taskId);
        });
    }

    /**
     * 更新任务步骤标签
     * @param taskEntity
     * @param currentStepLabel
     */
    private void updateTaskStepLabelEntity(ProjectTaskEntity taskEntity, String currentStepLabel) {
        taskEntity.setCurrentStepLabel(currentStepLabel);
        projectTaskService.updateById(taskEntity);
    }

    @NotNull
    private static List<Section> getSectionList(ProjectTaskEntity taskEntity) {
        String outlineStr = taskEntity.getOutline();
        JSONArray outline = JSONArray.parseArray(outlineStr);

        // 准备结果容器
        List<Section> flatSections = new ArrayList<>();

        // 第一步：将嵌套结构展平为顺序列表
        for (int i = 0; i < outline.size(); i++) {
            JSONObject mainSection = outline.getJSONObject(i);
            // 添加主章节
            flatSections.add(new Section(
                    mainSection.getString("label"),
                    mainSection.getString("description"),
                    true,  // 标记为主章节
                    i+1    // 主章节编号
            ));

            // 添加子章节
            JSONArray children = mainSection.getJSONArray("children");
            if (children != null) {
                for (int j = 0; j < children.size(); j++) {
                    JSONObject child = children.getJSONObject(j);
                    flatSections.add(new Section(
                            child.getString("label"),
                            child.getString("description"),
                            false, // 标记为子章节
                            i+1    // 所属主章节编号
                    ));
                }
            }
        }
        return flatSections;
    }

    private MessageTaskInfo createSummaryTaskInfo(PermissionQuery query,
                                           ProjectTaskEntity taskEntity,
                                           Long roleId,
                                           ExecuteTaskDto dto) {

        MessageTaskInfo taskInfo = new MessageTaskInfo();
        taskInfo.setOrgId(query.getOrgId());
        taskInfo.setDepartmentId(query.getDepartmentId());
        taskInfo.setOperatorId(query.getOperatorId());

        if (StringUtils.hasLength(taskEntity.getAttachments())) {
            List<Long> ids = Arrays.stream(taskEntity.getAttachments().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(ids);
            taskInfo.setAttachments(attachmentList);
        }

        taskInfo.setRoleId(roleId);
        taskInfo.setChannelStreamId(dto.getChannelStreamId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(taskEntity.getTaskGoal());

        return taskInfo;
    }

    private MessageTaskInfo createTaskInfo(PermissionQuery query,
                                           ProjectTaskEntity taskEntity,
                                           String label,
                                           String description,
                                           Long roleId,
                                           ExecuteTaskDto dto) {
        MessageTaskInfo taskInfo = new MessageTaskInfo();
        taskInfo.setOrgId(query.getOrgId());
        taskInfo.setDepartmentId(query.getDepartmentId());
        taskInfo.setOperatorId(query.getOperatorId());

        if (StringUtils.hasLength(taskEntity.getAttachments())) {
            List<Long> ids = Arrays.stream(taskEntity.getAttachments().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<FileAttachmentDto> attachmentList = cloudStorageConsumer.list(ids);
            taskInfo.setAttachments(attachmentList);
        }

        String promptText = ProjectPromptHandle.generatorExecutePrompt(
                StringUtils.hasLength(taskEntity.getTaskGoal()) ? taskEntity.getTaskGoal() : taskEntity.getTaskName(),
                taskEntity.getOutline(),
                label,
                description);

        taskInfo.setRoleId(roleId);
        taskInfo.setChannelStreamId(dto.getChannelStreamId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());
        taskInfo.setText(promptText);
        taskInfo.setQueryText(label + ":" + description);

        Long datasetGroupId = taskEntity.getDatasetGroupId();
        ProjectKnowledgeGroupEntity groupEntity = projectKnowledgeGroupService.getById(datasetGroupId);

        taskInfo.setCollectionIndexName(groupEntity.getVectorDatasetName());
        taskInfo.setCollectionIndexLabel(groupEntity.getGroupName());

        return taskInfo;
    }

    private String getFailureReason(Exception e) {
        if (e.getMessage() != null) {
            return e.getMessage().length() > 1000
                    ? e.getMessage().substring(0, 1000)
                    : e.getMessage();
        }
        return "Unknown error occurred during task execution";
    }

    private static class Section {
        String label;
        String description;
        boolean isMain;          // 是否为主章节
        int mainSectionNumber;   // 所属主章节编号

        Section(String label, String description, boolean isMain, int mainSectionNumber) {
            this.label = label;
            this.description = description;
            this.isMain = isMain;
            this.mainSectionNumber = mainSectionNumber;
        }
    }

    // 生成摘要
    private String generateSummary(String prompt, String content,
                                   PermissionQuery query,
                                   ProjectTaskEntity taskEntity,
                                   Long roleId,
                                   ExecuteTaskDto dto) throws Exception {
        return "生成摘要";
    }
}