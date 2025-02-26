package com.alinesno.infra.smart.assistant.plugin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.ToolRequestDto;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.plugin.mapper.ToolMapper;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return this.listByIds(toolIds).stream().map(tool -> {
                ToolDto dto = new ToolDto();
                BeanUtils.copyProperties(tool, dto);
                return dto;
            }).toList();
        }

        return null;
    }
}