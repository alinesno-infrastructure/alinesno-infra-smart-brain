package com.alinesno.infra.smart.assistant.scene.scene.productResearch.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.TextChatEditorDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.tools.LongTextChatRoleUtil;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ExecuteTaskDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectTaskGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.UpdateProjectReSearchTaskNameDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools.ProductResearchChatRoleUtil;
import com.alinesno.infra.smart.scene.entity.LongTextSceneEntity;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools.ProjectTaskExecutorService;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectResearchSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTaskService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.entity.ProjectResearchSceneEntity;
import com.alinesno.infra.smart.scene.entity.ProjectTaskEntity;
import com.alinesno.infra.smart.utils.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private ProductResearchChatRoleUtil productResearchChatRoleUtil ;

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
        taskEntity.setTaskGoal(dto.getPromptText());
        taskEntity.setTaskDescription(dto.getPromptText());
        taskEntity.setSceneId(dto.getSceneId());
        taskEntity.setTaskStartTime(new Date());
        taskEntity.setTaskStatus(TaskStatusEnum.NOT_RUN.getCode());
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

        if (taskEntity.getTaskStatus().equals(TaskStatusEnum.RUNNING.getCode())) {
            return AjaxResult.success("任务正在执行中" , taskEntity);
        }

        if (taskEntity.getTaskStatus().equals(TaskStatusEnum.RUN_COMPLETED.getCode())) {
            return AjaxResult.success("任务已完成" , taskEntity);
        }

        if(TaskStatusEnum.RUN_FAILED.getCode().equals(taskEntity.getTaskStatus())){
            return AjaxResult.success("任务运行已失败，请勿重复执行" , taskEntity) ;
        }

        taskEntity.setChannelStreamId(dto.getChannelStreamId());
        projectTaskService.updateById(taskEntity);

        projectTaskExecutorService.executeTaskAsync(dto, query, taskEntity);

        return AjaxResult.success("任务已开始执行" , taskEntity);
    }

    /**
     * 更新任务名称
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/updateTaskName")
    public AjaxResult updateTaskName(@RequestBody @Validated UpdateProjectReSearchTaskNameDto dto , PermissionQuery query) {

        ProjectTaskEntity task = projectTaskService.getById(dto.getTaskId());

        if(task == null){
            return AjaxResult.error("任务不存在");
        }

        task.setTaskName(dto.getTaskName());
        projectTaskService.updateById(task);

        return AjaxResult.success("任务名称已更新");
    }

    /**
     * 根据id删除任务
     * @return
     */
    @DataPermissionQuery
    @DeleteMapping("/deleteTaskById")
    public AjaxResult deleteTaskById(@RequestParam Long taskId) {
        projectTaskService.removeById(taskId);
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 与编辑角色沟通
     * @param dto
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/chatEditorRole")
    public AjaxResult chatEditorRole(@RequestBody @Validated TextChatEditorDto dto , PermissionQuery query){

        ProjectResearchSceneEntity entity = projectResearchSceneService.getBySceneId(dto.getSceneId(), query) ;
        String articleWriterEngineer = entity.getProgressAnalyzerEngineer() + StringUtils.EMPTY;

        IndustryRoleDto roleDto =  RoleUtils.getEditors(roleService , articleWriterEngineer).get(0) ;
        productResearchChatRoleUtil.chat(roleDto , dto , query) ;

        return AjaxResult.success("操作成功") ;
    }

    @Override
    public IProjectTaskService getFeign() {
        return this.projectTaskService;
    }
}