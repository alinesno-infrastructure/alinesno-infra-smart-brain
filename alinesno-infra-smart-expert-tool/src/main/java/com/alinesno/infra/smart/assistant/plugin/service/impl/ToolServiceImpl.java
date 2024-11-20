package com.alinesno.infra.smart.assistant.plugin.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.ToolRequestDto;
import com.alinesno.infra.smart.assistant.entity.RoleToolEntity;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.plugin.mapper.ToolMapper;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.service.IRoleToolService;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
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

    @Autowired
    private IRoleToolService roleToolService ;

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

            Object output = ToolExecutor.executeGroovyScript(groovyScript, paramMap);
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

    @Override
    public List<ToolDto> getByRole(long roleId) {

        List<ToolEntity> roleTools = roleToolService.findTools(roleId) ;

        return roleTools.stream().map(tool -> {
            ToolDto dto = new ToolDto();
            BeanUtils.copyProperties(tool, dto);
            return dto;
        }).toList();

    }

    @Override
    public ToolEntity getToolScript(String toolFullName, Long roleId) {

        LambdaQueryWrapper<RoleToolEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleToolEntity::getRoleId, roleId);
        List<RoleToolEntity> roleTools = roleToolService.list(queryWrapper) ;

        List<ToolEntity> tools = this.listByIds(roleTools.stream().map(RoleToolEntity::getToolId).toList()) ;
        for (ToolEntity tool : tools) {
            if(tool.getToolFullName().equals(toolFullName)){
                return tool ;
            }
        }

        return null;
    }
}