package com.alinesno.infra.smart.assistant.plugin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.ToolRequestDto;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.enums.ModelDataScopeOptions;
import com.alinesno.infra.smart.assistant.plugin.mapper.ToolMapper;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class ToolServiceImpl extends IBaseServiceImpl<ToolEntity, ToolMapper> implements IToolService {

//    @Autowired
//    private IRoleToolService roleToolService ;

    @Override
    public String validateToolScript(ToolRequestDto dto) {

        String groovyScript = dto.getScript() ;
        ToolEntity toolEntity = getById(dto.getToolId()) ;
        toolEntity.setUseCount(toolEntity.getUseCount() == null ? 1 : toolEntity.getUseCount()+1) ;

        try{
            String paramStr =  dto.getParams() ;

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> paramMap = gson.fromJson(paramStr, type);

            Object output = ToolExecutor.executeGroovyScript(groovyScript, paramMap, new HashMap<>());
            log.debug("工具执行结果：{}", output);

            // 更新工具使用次数
            toolEntity.setSuccessCount(toolEntity.getSuccessCount() == null ? 1 : toolEntity.getSuccessCount()+1);
            update(toolEntity);

            return String.valueOf(output) ;
        }catch (Exception e){

            toolEntity.setErrorCount(toolEntity.getErrorCount() == null ? 1 : toolEntity.getErrorCount()+1);
            update(toolEntity);

            log.error("工具执行异常：{}", e.getMessage(), e);
            return "角色脚本执行失败:" + e.getMessage() ;
        }


    }

//    @Override
//    public List<ToolDto> getByRole(long roleId) {
//
////        List<ToolEntity> roleTools = roleToolService.findTools(roleId) ;
////
////        return roleTools.stream().map(tool -> {
////            ToolDto dto = new ToolDto();
////            BeanUtils.copyProperties(tool, dto);
////            return dto;
////        }).toList();
//
//        return null ;
//
//    }

    @Override
    public ToolEntity getToolScript(String toolFullName, String selectionToolsData) {

        List<Long> toolIds = JSON.parseArray(selectionToolsData, Long.class) ;

        if(toolIds == null || toolIds.isEmpty()){
            return null ;
        }

        List<ToolEntity> tools = this.listByIds(toolIds) ;

        for (ToolEntity tool : tools) {
            if(tool.getToolFullName().equals(toolFullName)){
                return tool ;
            }
        }

        return null ;
    }

    @Override
    public List<ToolDto> getByToolIds(String selectionToolsData) {

        if(StringUtils.isNotEmpty(selectionToolsData)){
            List<Long> toolIds = JSON.parseArray(selectionToolsData, Long.class) ;
            if(toolIds != null && !toolIds.isEmpty()){
                return this.listByIds(toolIds).stream().map(tool -> {
                    ToolDto dto = new ToolDto();
                    BeanUtils.copyProperties(tool, dto);
                    return dto;
                }).toList();
            }
        }

        return null;
    }

    @Override
    public TableDataInfo toolSelection(DatatablesPageBean page, PermissionQuery query) {

        // TODO: 待处理分页的问题
        int pageNow = page.getPageNum();
        int pageSize = page.getPageSize();

        LambdaQueryWrapper<ToolEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.setEntityClass(ToolEntity.class) ;
        query.toWrapper(wrapper);

        Page<ToolEntity> pageResult = new Page<>(pageNow, pageSize) ;
        pageResult = this.page(pageResult, wrapper);

        // 查询出公共的工具
        LambdaQueryWrapper<ToolEntity> publicWrapper = new LambdaQueryWrapper<>();
        publicWrapper.eq(ToolEntity::getToolPermission, ModelDataScopeOptions.PUBLIC.getValue());
        List<ToolEntity> publicTools = this.list(publicWrapper);

        if(publicTools == null){
            publicTools = new ArrayList<>();
        }

        TableDataInfo tableDataInfo = new TableDataInfo();

        publicTools.addAll(pageResult.getRecords())  ;
        tableDataInfo.setRows(publicTools);

        tableDataInfo.setTotal(pageResult.getTotal() + publicTools.size());

        return tableDataInfo;
    }
}