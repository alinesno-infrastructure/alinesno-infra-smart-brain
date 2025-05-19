package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.ToolRequestDto;
import com.alinesno.infra.smart.assistant.api.ToolResult;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;

import java.util.List;
import java.util.Map;

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

//    /**
//     * 根据角色获取工具
//     * @param roleId
//     * @return
//     */
//    @Deprecated
//    List<ToolDto> getByRole(long roleId);

    /**
     * 通过工具名，查询工具脚本
     * @param toolFullName
     * @param selectionToolsData
     * @return
     */
    ToolEntity getToolScript(String toolFullName, String selectionToolsData);

    /**
     * 通过工具id，查询工具列表
     * @param selectionToolsData
     * @return
     */
    List<ToolDto> getByToolIds(String selectionToolsData, Long orgId);

    /**
     * 工具列表
     * @param page
     * @param query
     * @return
     */
    TableDataInfo toolSelection(DatatablesPageBean page, PermissionQuery query);

    /**
     * 执行远程MCP工具
     *
     * @param id
     * @param argsList
     * @param orgId
     * @return
     */
    ToolResult executeMcpTool(String id, Map<String, String> argsList, Long orgId);
}