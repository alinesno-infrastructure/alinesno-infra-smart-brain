package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.ToolRequestDto;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;

import java.util.List;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IToolService extends IBaseService<ToolEntity> {

    /**
     * 验证工具脚本
     * @param dto
     * @return
     */
    String validateToolScript(ToolRequestDto dto);

    /**
     * 根据角色获取工具
     * @param roleId
     * @return
     */
    List<ToolDto> getByRole(long roleId);
}