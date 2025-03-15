package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.TestLlmModelDto;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.enums.LlmModelProviderEnums;
import com.alinesno.infra.smart.assistant.enums.ModelTypeEnums;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Api(tags = "LlmModel")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/llmModel")
public class LlmModelController extends BaseController<LlmModelEntity, ILlmModelService> {

    @Autowired
    private ILlmModelService service;

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
        TableDataInfo list =  this.toPage(model, this.getFeign(), page);

        if(!CollectionUtils.isEmpty(list.getRows())){
            List<LlmModelEntity> rows = (List<LlmModelEntity>) list.getRows();
            for(LlmModelEntity entity : rows) {
                entity.setFieldProp(ModelTypeEnums.getDisplayNameByCode(entity.getModelType())) ;
            }
            list.setRows(rows);
        }

        return list ;
    }

    @DataPermissionSave
    @Override
    public AjaxResult save(Model model, @RequestBody LlmModelEntity entity) throws Exception {
        log.debug("entity = {}", ToStringBuilder.reflectionToString(entity));
        return super.save(model, entity);
    }

    /**
     * 获取所有大模型提供商信息
     * @return 包含所有大模型提供商信息的列表
     */
    @GetMapping("/getAllModelProvidersInfo")
    public AjaxResult getAllModelProvidersInfo() {
        List<Map<String, Object>> list =  LlmModelProviderEnums.getAllModelProvidersInfo();
        return AjaxResult.success(list);
    }

    @GetMapping("/getAllModelTypesInfo")
    public AjaxResult getAllModelTypesInfo() {
        List<Map<String, Object>> modelTypesInfo = ModelTypeEnums.getAllModelTypesInfo();
        return AjaxResult.success(modelTypesInfo);
    }

    /**
     * 列出所有配置的大模型
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/listLlmMode")
    public AjaxResult listLlmMode(PermissionQuery query , String modelType) {

        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(LlmModelEntity.class) ;
        query.toWrapper(queryWrapper);

        if(StringUtils.isNotEmpty(modelType)){  // 根据类型筛选
            queryWrapper.eq(LlmModelEntity::getModelType , modelType) ;
        }

        List<LlmModelEntity> allLlmMode = service.list(queryWrapper);

        if(!CollectionUtils.isEmpty(allLlmMode)){
            for(LlmModelEntity entity : allLlmMode) {
                entity.setFieldProp(ModelTypeEnums.getDisplayNameByCode(entity.getModelType())) ;
            }
        }

        return AjaxResult.success(allLlmMode) ;
    }

    /**
     * 模型测试
     * @return
     */
    @PostMapping("/testLlmModel")
    public AjaxResult testLlmModel(@RequestBody @Validated TestLlmModelDto dto) {
        log.debug("dto = {}" , dto);
        String result = service.testLlmModel(dto) ;
        return AjaxResult.success("操作成功" , result) ;
    }

    @Override
    public ILlmModelService getFeign() {
        return this.service;
    }
}