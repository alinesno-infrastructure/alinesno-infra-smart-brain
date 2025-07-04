package com.alinesno.infra.smart.assistant.scene.scene.productResearch.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ExecuteTaskDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectTaskGeneratorDTO;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.job.ProjectTaskExecutorService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectResearchSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.entity.ProjectResearchSceneEntity;
import com.alinesno.infra.smart.scene.entity.ProjectTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 项目任务管理
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/projectTask")
public class ProjectTaskController extends BaseController<ProjectTaskEntity, IProjectTaskService> {

    @Autowired
    private IProjectTaskService projectTaskService;

    @Autowired
    private IProjectResearchSceneService projectResearchSceneService;

    @Autowired
    private IProjectKnowledgeGroupService projectKnowledgeGroupService ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer ;

    @Autowired
    private ProjectTaskExecutorService projectTaskExecutorService ;

    /**
     * 更新chapterPromptContent内容
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/addTask")
    public AjaxResult addTask(@RequestBody @Validated ProjectTaskGeneratorDTO dto , PermissionQuery query) {

        ProjectResearchSceneEntity sceneEntity = projectResearchSceneService.getBySceneId(dto.getSceneId() , query) ;

        ProjectTaskEntity taskEntity = new ProjectTaskEntity() ;
        BeanUtil.copyProperties(query , taskEntity);

        taskEntity.setTaskName(dto.getPromptText());
        taskEntity.setTaskDescription(dto.getPromptText());
        taskEntity.setSceneId(dto.getSceneId());
        taskEntity.setTaskStartTime(new Date());
        taskEntity.setTaskStatus(0);
        taskEntity.setOutline(JSONObject.toJSONString(dto.getOutline()));

        taskEntity.setProjectResearchSceneId(sceneEntity.getId());
        taskEntity.setDatasetGroupId(dto.getDatasetGroupId());

        if(dto.getAttachments() != null){
            String attachments = dto.getAttachments().stream().map(String::valueOf).collect(Collectors.joining(",")) ;
            taskEntity.setAttachments(attachments); // 附件ID号，以逗号进行分隔
        }

        projectTaskService.save(taskEntity);

        return AjaxResult.success("操作成功" , taskEntity.getId()) ;
    }

    @DataPermissionQuery
    @PostMapping("/executeTask")
    public AjaxResult executeTask(@RequestBody @Validated ExecuteTaskDto dto, PermissionQuery query) {
        ProjectTaskEntity taskEntity = projectTaskService.getById(dto.getTaskId());

        if("reGenerate".equals(dto.getType())){  // 重新生成
            projectTaskExecutorService.executeTaskAsync(dto, query, taskEntity);
            return AjaxResult.success("任务已开始执行" , taskEntity);
        }

        if (taskEntity == null) {
            return AjaxResult.error("任务不存在");
        }

        if (taskEntity.getTaskStatus() == TaskStatusEnum.RUNNING.getCode()) {
            return AjaxResult.success("任务正在执行中" , taskEntity);
        }

        if (taskEntity.getTaskStatus() == TaskStatusEnum.RUN_COMPLETED.getCode()) {
            return AjaxResult.success("任务已完成" , taskEntity);
        }

        taskEntity.setChannelStreamId(dto.getChannelStreamId());
        projectTaskService.updateById(taskEntity);

        projectTaskExecutorService.executeTaskAsync(dto, query, taskEntity);

        return AjaxResult.success("任务已开始执行" , taskEntity);
    }

    @Override
    public IProjectTaskService getFeign() {
        return this.projectTaskService;
    }
}