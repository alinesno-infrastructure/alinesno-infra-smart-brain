package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.DeepSearchTaskUtils;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import com.alinesno.infra.smart.scene.dto.DeepsearchChatRoleDto;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 深度搜索任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/deepSearchTask/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class DeepSearchTaskController extends BaseController<DeepSearchTaskEntity, IDeepSearchTaskService> {

    @Autowired
    private IDeepSearchTaskService taskService ;

    @Autowired
    private DeepSearchTaskUtils  deepSearchTaskUtils ;

    /**
     * 查询出所有的DeepSearch场景
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/pagerListByPage")
    public AjaxResult pagerListByPage(PermissionQuery query , @RequestParam(required = true) String sceneId) {

        LambdaQueryWrapper<DeepSearchTaskEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(DeepSearchTaskEntity.class);
        query.toWrapper(queryWrapper);
        queryWrapper.eq(DeepSearchTaskEntity::getSceneId, sceneId);
        queryWrapper.orderByDesc(DeepSearchTaskEntity::getAddTime);

        // 使用分页查询，设置每页大小为20
        Page<DeepSearchTaskEntity> page = new Page<>(1, 20);

        return AjaxResult.success(taskService.page(page , queryWrapper).getRecords()) ;
    }

    /**
     * 提交任务
     * @param chatRole
     * @param query
     * @return
     */
    @ScenePointAnnotation(sceneCode = SceneEnum.DEEP_SEARCH)
    @DataPermissionSave
    @PostMapping("/submitDeepSearchTask")
    public AjaxResult submitDeepSearchTask(@RequestBody @Validated DeepsearchChatRoleDto chatRole, PermissionQuery query) {

        Long taskId = chatRole.getTaskId();
        String channelStreamId = chatRole.getChannelStreamId();

        DeepSearchTaskEntity task = taskService.getById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        // 检查任务状态是否可执行
        if (task.getTaskStatus() != null && !(TaskStatusEnum.NOT_RUN.getCode().equals(task.getTaskStatus()))) {
            return AjaxResult.error("任务状态不允许执行");
        }

        // 更新任务状态为运行中
        task.setTaskStatus(String.valueOf(TaskStatusEnum.RUNNING.getCode()));
        task.setTaskStartTime(new Date());
        task.setChannelStreamId(channelStreamId);

        taskService.updateById(task);

        // 异步执行任务
        deepSearchTaskUtils.executeTaskAsync(chatRole , query);

        // 创建一个消息对象
        ChatMessageDto msgDto = deepSearchTaskUtils.getChatMessageDto(chatRole , CurrentAccountJwt.get()) ;

        return AjaxResult.success(msgDto);
    }

    @Override
    public IDeepSearchTaskService getFeign() {
        return this.taskService;
    }

}
