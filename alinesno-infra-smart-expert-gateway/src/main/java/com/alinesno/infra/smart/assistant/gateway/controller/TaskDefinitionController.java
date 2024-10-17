package com.alinesno.infra.smart.assistant.gateway.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.task.ParamsDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessDefinitionDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessTaskDto;
import com.alinesno.infra.smart.assistant.api.task.ProcessTaskValidateDto;
import com.alinesno.infra.smart.assistant.entity.TaskDefinitionEntity;
import com.alinesno.infra.smart.assistant.enums.ExecutorTypeEnums;
import com.alinesno.infra.smart.assistant.gateway.session.CurrentProjectSession;
import com.alinesno.infra.smart.assistant.service.ITaskDefinitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理与TaskDefinitionEntity相关的请求的Controller。
 * 继承自BaseController类并实现ITaskDefinitionService接口。
 *
 * @version 1.0.0
 * @author  luoxiaodong
 */
@Slf4j
@Api(tags = "TaskDefinition")
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/taskDefinition")
public class TaskDefinitionController extends BaseController<TaskDefinitionEntity, ITaskDefinitionService> {

    @Autowired
    private ITaskDefinitionService service;

    /**
     * 获取TaskDefinitionEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    // 通过processDefinitionId实现类获取到任务的定义
    @GetMapping("/getTaskDefinition")
    public AjaxResult getTaskDefinition(long roleId) {

        log.debug("processDefinition:{}" , roleId);

        LambdaQueryWrapper<TaskDefinitionEntity> query = new LambdaQueryWrapper<>();
        query.eq(TaskDefinitionEntity::getRoleId, roleId);

        List<TaskDefinitionEntity> list = service.list(query);

        // Entity转换成DTO
        List<ProcessTaskDto> dtoList = new ArrayList<>() ;
        for (TaskDefinitionEntity entity : list) {
            ProcessTaskDto dto = new ProcessTaskDto() ;

            dto.setTaskId(entity.getId());
            dto.setId(entity.getId()+"");
            dto.setName(entity.getName()) ;
            dto.setDescription(entity.getDescription()) ;

            ExecutorTypeEnums executorTypeEnums =  ExecutorTypeEnums.fromCode(entity.getTaskType()) ;
            dto.setType(executorTypeEnums.getType());

            if(entity.getAttr()!= null){
                dto.setAttr(JSONObject.parseObject(entity.getAttr()));
            }

            if(entity.getTaskParams() != null){
                ParamsDto paramsDto = JSONObject.parseObject(entity.getTaskParams(), ParamsDto.class);
                dto.setParams(paramsDto);
            }

            dtoList.add(dto) ;
        }

        return AjaxResult.success(dtoList) ;
    }

    /**
     * 验证脚本任务
     * @param dto
     * @return
     */
    @PostMapping("/validateTask")
    public AjaxResult validateTask(@RequestBody @Validated ProcessTaskValidateDto dto){

        log.debug("dto = {}", JSONUtil.toJsonPrettyStr(JSONObject.toJSONString(dto)));

        // 运行任务验证
        service.runProcessTask(dto) ;

        return AjaxResult.success() ;
    }

    /**
     * 保存流程定义信息
     * @return
     */
    @PostMapping("/commitProcessDefinition")
    public AjaxResult commitProcessDefinition(@RequestBody ProcessDefinitionDto dto , String type){
        log.debug("type = {} , dto = {}", type , dto);

        List<ProcessTaskDto> taskFlow = dto.getTaskFlow() ;
        Assert.isTrue(taskFlow.size() > 1 , "流程定义为空,请定义流程.");

        dto.setProjectId(CurrentProjectSession.get().getId());

        long processId = type.equals("validate")? service.validateProcessDefinition(dto) : service.commitProcessDefinition(dto) ;
        return AjaxResult.success("success" , processId) ;
    }


    @Override
    public ITaskDefinitionService getFeign() {
        return this.service;
    }
}
