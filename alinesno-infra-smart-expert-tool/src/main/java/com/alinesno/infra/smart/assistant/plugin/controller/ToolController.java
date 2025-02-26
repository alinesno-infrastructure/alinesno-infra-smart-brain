package com.alinesno.infra.smart.assistant.plugin.controller;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.ToolRequestDto;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Api(tags = "Channel")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/tool")
public class ToolController extends BaseController<ToolEntity, IToolService> {

    @Autowired
    private IToolService service;

    /**
     * 获取ApplicationEntity的DataTables数据
     * 
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    @DataPermissionSave
    @ResponseBody
    @PostMapping("/saveTool")
    public AjaxResult saveTool(@RequestBody ToolEntity entity) throws Exception {

//        ToolTypeEnums type =  ToolTypeEnums.fromKey(entity.getToolType()) ;
//        Assert.notNull(type , "工具类型错误");

        service.save(entity);
        return this.ok();
    }

    /**
     * 查询到所有的工具，并返回DTO
     */
    @DataPermissionQuery
    @GetMapping("/getAllTool")
    public AjaxResult getAllTool(PermissionQuery query) {

        LambdaQueryWrapper<ToolEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(ToolEntity.class);
        query.toWrapper(queryWrapper);

        List<ToolEntity> allTool = service.list(queryWrapper);
        List<ToolDto> allToolDto = new ArrayList<>() ;

        for (ToolEntity toolEntity : allTool) {
            ToolDto toolDto = new ToolDto();
            BeanUtils.copyProperties(toolEntity, toolDto);
            allToolDto.add(toolDto);
        }

        return AjaxResult.success("操作成功" ,allToolDto) ;
    }

    /**
     * 更新工具脚本
     * @return
     */
    @PostMapping("/updateToolScript")
    public AjaxResult updateToolScript(@RequestBody @Valid ToolRequestDto dto) {
        ToolEntity entity = service.getById(dto.getToolId());

        entity.setGroovyScript(dto.getScript());

        String toolInfo = ToolExecutor.getToolInfo(dto.getScript());

        entity.setToolInfo(toolInfo) ;
        entity.setToolFullName(JSONObject.parseObject(toolInfo , JSONObject.class).getString("name"));

        service.update(entity);

        return ok();
    }

    /**
     * 验证工具脚本
     * @return
     */
    @PostMapping("/validateToolScript")
    public AjaxResult validateToolScript(@RequestBody @Valid ToolRequestDto dto) {

        String output = service.validateToolScript(dto);

        return AjaxResult.success("操作成功" , output);
    }

    @Override
    public IToolService getFeign() {
        return this.service;
    }
}