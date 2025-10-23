package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskRecordService;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.DeepSearchTaskUtils;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.RebuildFlowUtils;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import com.alinesno.infra.smart.scene.dto.DeepsearchChatRoleDto;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskRecordEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private IDeepSearchTaskRecordService taskRecordService ;

    @Autowired
    private DeepSearchTaskUtils  deepSearchTaskUtils ;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

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
    @SneakyThrows
    @ScenePointAnnotation(sceneCode = SceneEnum.DEEP_SEARCH)
    @DataPermissionSave
    @PostMapping("/submitDeepSearchTask")
    public AjaxResult submitDeepSearchTask(@RequestBody @Validated DeepsearchChatRoleDto chatRole, PermissionQuery query) {

        Long taskId = chatRole.getTaskId();
        String channelStreamId = chatRole.getChannelStreamId();
        String sessionId = chatRole.getSessionId();

        DeepSearchTaskEntity task = taskService.getById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        if(task.getTaskStatus().equals(String.valueOf(TaskStatusEnum.RUNNING.getCode()))){
            return AjaxResult.success("任务正在运行中，请勿重复提交");
        }

        // 更新任务状态为运行中
        task.setTaskStatus(String.valueOf(TaskStatusEnum.RUNNING.getCode()));
        task.setTaskStartTime(new Date());
        task.setChannelStreamId(channelStreamId);

        taskService.updateById(task);

        if(StringUtils.isNotEmpty(task.getAttachments())){
            chatRole.setAttachments(deepSearchTaskUtils.mergeAttachments(chatRole.getAttachments() , task.getAttachments()));
        }

        // 异步执行任务
        deepSearchTaskUtils.executeTaskAsync(chatRole , query , sessionId);

        // 创建一个消息对象
        ChatMessageDto msgDto = deepSearchTaskUtils.getChatMessageDto(chatRole , CurrentAccountJwt.get()) ;
        msgDto.setSessionId(sessionId);

        // 从实体类中获取 attachments 字符串（如 task.getAttachments()）
        String attachmentsStr = task.getAttachments();

        if (attachmentsStr != null && !attachmentsStr.isEmpty()) {
            // 分割字符串并转换为 List<Long>
            List<Long> attachments = Arrays.stream(attachmentsStr.split(","))
                    .map(Long::parseLong) // 字符串转 Long（若格式错误会抛异常）
                    .collect(Collectors.toList());

            // 调用方法处理
            msgDto.setFileAttributeList(storageConsumer.list(attachments));
        } else {
            // 若字符串为空，可设置空集合或做其他处理
            msgDto.setFileAttributeList(Collections.emptyList());
        }

        return AjaxResult.success(msgDto);
    }

    /**
     * 获取任务完整记录
     */
    @GetMapping("/getTaskRecords")
    public AjaxResult getTaskRecords(@RequestParam Long taskId) {
        try {
            // 查询任务基本信息
            DeepSearchTaskEntity task = taskService.getById(taskId);
            if (task == null) {
                return AjaxResult.error("任务不存在");
            }

            // 查询任务的所有记录
            LambdaQueryWrapper<DeepSearchTaskRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeepSearchTaskRecordEntity::getTaskId, taskId);
            queryWrapper.orderByAsc(DeepSearchTaskRecordEntity::getSeq); // 按顺序排序

            List<DeepSearchTaskRecordEntity> records = taskRecordService.list(queryWrapper);

            // 构建完整的 DeepSearchFlow 对象
            DeepSearchFlow deepSearchFlow = RebuildFlowUtils.rebuildDeepSearchFlowFromRecords(records, task);

            AjaxResult result =  AjaxResult.success(deepSearchFlow);
            result.put("username" , CurrentAccountJwt.get().getName()) ;

            return result;
        } catch (Exception e) {
            log.error("获取任务记录失败", e);
            return AjaxResult.error("获取任务记录失败");
        }
    }

    @Override
    public IDeepSearchTaskService getFeign() {
        return this.taskService;
    }

}
