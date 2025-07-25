package com.alinesno.infra.smart.assistant.scene.scene.longtext.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.LongTextTaskGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.dto.TaskStatusDto;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.tools.LongTextTaskExecutionService;
import com.alinesno.infra.smart.scene.entity.LongTextSceneEntity;
import com.alinesno.infra.smart.scene.entity.LongTextTaskEntity;
import com.alinesno.infra.smart.scene.enums.TaskStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/longTextTask")
public class LongTextTaskController extends BaseController<LongTextTaskEntity, ILongTextTaskService> {

    @Autowired
    private ILongTextTaskService service;

    @Autowired
    private ILongTextSceneService generalAgentSceneService ;

    @Autowired
    private LongTextTaskExecutionService taskExecutionService;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }


    /**
     * 更新chapterPromptContent内容
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/addTask")
    public AjaxResult addTask(@RequestBody @Validated LongTextTaskGeneratorDTO dto , PermissionQuery query) {

        LongTextSceneEntity sceneEntity = generalAgentSceneService.getBySceneId(dto.getSceneId() , query) ;

        LongTextTaskEntity taskEntity = new LongTextTaskEntity() ;
        BeanUtil.copyProperties(query , taskEntity);

        taskEntity.setTaskName(dto.getPromptText());
        taskEntity.setTaskDescription(dto.getPromptText());
        taskEntity.setSceneId(dto.getSceneId());
        taskEntity.setLongTextSceneId(sceneEntity.getId());
        taskEntity.setTaskStartTime(new Date());
        taskEntity.setSelectedTemplateId(dto.getSelectedTemplateId());
        taskEntity.setChannelStreamId(dto.getChannelStreamId());
        taskEntity.setGenStatus(0);

        if(dto.getAttachments() != null){
            String attachments = dto.getAttachments().stream().map(String::valueOf).collect(Collectors.joining(",")) ;
            taskEntity.setAttachments(attachments); // 附件ID号，以逗号进行分隔
        }

        service.save(taskEntity);

        return AjaxResult.success("操作成功" , taskEntity.getId()) ;
    }


    /**
     * 分页查询试题(pagerListByPage)
     */
    @DataPermissionQuery
    @PostMapping("/pagerListByPage")
    public AjaxResult pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId) {
        List<LongTextTaskEntity> list = service.pagerListByPage(page, query , sceneId);
        return AjaxResult.success("操作成功." ,list);
    }

    /**
     * 通过任务id获取到任务详情
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getLongTextTask")
    public AjaxResult getLongTextTask(@RequestParam Long taskId , PermissionQuery query) {

        Assert.notNull(taskId, "任务ID不能为空");

        LambdaQueryWrapper<LongTextTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LongTextTaskEntity::getId , taskId);
        wrapper.eq(LongTextTaskEntity::getOrgId, query.getOrgId());

        LongTextTaskEntity entity = service.getOne(wrapper);

        return AjaxResult.success("操作成功." ,entity);
    }

    /**
     * 更新生成状态
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/updateTaskGenStatus")
    public AjaxResult updateTaskGenStatus(@RequestParam("taskId") long taskId,
                                           @RequestParam("genStatus") String genStatus,
                                           PermissionQuery query) {

        LongTextTaskEntity taskEntity = service.getById(taskId) ;
        taskEntity.setTaskStatus(genStatus);

        service.updateById(taskEntity);

        return AjaxResult.success("操作成功") ;
    }

    /**
     * 提交任务执行
     * @param taskId 任务ID
     * @return 操作结果
     */
    @DataPermissionQuery
    @GetMapping("/submitTask")
    public AjaxResult submitTask(@RequestParam Long taskId, @RequestParam Long channelStreamId ,  PermissionQuery query) {
        LongTextTaskEntity task = service.getById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        // 检查任务状态是否可执行
        if (task.getTaskStatus() != null && !(TaskStatusEnum.NOT_RUN.getCode() == Integer.parseInt(task.getTaskStatus()))) {
            return AjaxResult.error("任务状态不允许执行");
        }

        // 更新任务状态为运行中
        task.setTaskStatus(String.valueOf(TaskStatusEnum.RUNNING.getCode()));
        task.setTaskStartTime(new Date());
        task.setChannelStreamId(String.valueOf(channelStreamId));

        service.updateById(task);

        // 异步执行任务
        taskExecutionService.executeTaskAsync(taskId, channelStreamId , query);

        return AjaxResult.success("任务已开始执行");
    }

    /**
     * 提交章节任务
     */
    @DataPermissionQuery
    @GetMapping("/submitChapterTask")
    public AjaxResult submitChapterTask(@RequestParam Long taskId, @RequestParam Long channelStreamId ,  PermissionQuery query) {
        LongTextTaskEntity task = service.getById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        // 检查任务状态是否可执行
        if (task.getChapterStatus() != null && !(TaskStatusEnum.RUN_COMPLETED.getCode() == Integer.parseInt(task.getChapterStatus()))) {
            return AjaxResult.error("单节任务已生成完成.");
        }

        // 更新任务状态为运行中
        task.setChapterStatus(String.valueOf(TaskStatusEnum.RUNNING.getCode()));
        task.setTaskStartTime(new Date());

        service.updateById(task);

        // 异步执行任务
        taskExecutionService.executeChapterTask(taskId, query, task);

        return AjaxResult.success("章节任务已开始执行");
    }


    /**
     * 查询任务状态
     * @param taskId 任务ID
     * @return 任务状态信息
     */
    @DataPermissionQuery
    @GetMapping("/getTaskStatus")
    public AjaxResult getTaskStatus(@RequestParam Long taskId) {
        LongTextTaskEntity task = service.getById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        // 返回任务状态和进度信息
        TaskStatusDto statusDto = new TaskStatusDto();
        statusDto.setTaskId(taskId);
        statusDto.setStatus(task.getTaskStatus());
        statusDto.setProgress(taskExecutionService.getTaskProgress(taskId));

        return AjaxResult.success(statusDto);
    }

    /**
     * 停止任务执行
     * @param taskId 任务ID
     * @return 操作结果
     */
    @DataPermissionQuery
    @GetMapping("/stopTask")
    public AjaxResult stopTask(@RequestParam Long taskId) {
        boolean cancelled = taskExecutionService.cancelTask(taskId);

        if (cancelled) {
            // 更新数据库状态
            LongTextTaskEntity task = service.getById(taskId);
            task.setTaskStatus(String.valueOf(TaskStatusEnum.CANCELLED.getCode()));
            service.updateById(task);
            return AjaxResult.success("任务已停止");
        }
        return AjaxResult.error("停止任务失败，任务可能已完成或不存在");
    }

    @Override
    public ILongTextTaskService getFeign() {
        return this.service;
    }
}