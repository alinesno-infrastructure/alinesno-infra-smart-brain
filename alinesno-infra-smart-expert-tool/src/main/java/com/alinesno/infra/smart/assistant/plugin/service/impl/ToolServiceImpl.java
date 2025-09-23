package com.alinesno.infra.smart.assistant.plugin.service.impl;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.ToolRequestDto;
import com.alinesno.infra.smart.assistant.api.ToolResult;
import com.alinesno.infra.smart.assistant.entity.McpConfigEntity;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.plugin.mapper.ToolMapper;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.service.IMcpConfigService;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IMcpConfigService mcpConfigService;

    @Override
    public String validateToolScript(ToolRequestDto dto) {

        String groovyScript = dto.getScript() ;
        ToolEntity toolEntity = getById(dto.getToolId()) ;
        toolEntity.setUseCount(toolEntity.getUseCount() == null ? 1 : toolEntity.getUseCount()+1) ;

        try{
            String paramStr =  dto.getParams() ;

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> paramMap = gson.fromJson(paramStr, type);

            paramMap.put("accountId", String.valueOf(dto.getOperatorId()));
            paramMap.put("accountOrgId", String.valueOf(dto.getOrgId()));

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
    public List<ToolDto> getByToolIds(String selectionToolsData, Long orgId) {

        if(StringUtils.isNotEmpty(selectionToolsData)){
            List<Long> toolIds = JSON.parseArray(selectionToolsData, Long.class) ;
            if(toolIds != null && !toolIds.isEmpty()){

                // 本地工具包
                List<ToolDto> localTools = this.listByIds(toolIds).stream().map(tool -> {
                    ToolDto dto = new ToolDto();
                    BeanUtils.copyProperties(tool, dto);
                    return dto;
                }).toList();

                // 创建一个可变的ArrayList，并将本地工具添加进去
                List<ToolDto> allTools = new ArrayList<>(localTools);

                // MCP远程工具包
                List<ToolDto> mcpTools = listMcpTool(toolIds , orgId) ;
                if(CollectionUtils.isNotEmpty(mcpTools)){
                    allTools.addAll(mcpTools); // 现在可以安全地添加元素
                }

                return allTools ;
            }
        }

        return null;
    }

    /**
     * 查询MCP工具包
     * @param toolIds
     * @return
     */
    private List<ToolDto> listMcpTool(List<Long> toolIds  , Long orgId) {

        McpConfigEntity mcpConfigEntity = mcpConfigService.getByOrgId(orgId) ;
        if(mcpConfigEntity == null){
            return Collections.emptyList();
        }

        // 构建请求URL
        String baseUrl = mcpConfigEntity.getMcpUrl().trim();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        String url = baseUrl + "mcp/selectTool";

        // 发送HTTP请求
        HttpResponse response = HttpUtil.createPost(url)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(com.alibaba.fastjson.JSONObject.toJSONString(toolIds))
                .execute();

        // 处理响应
        if (response.isOk()) {
            String responseBody = response.body();
            R<List<ToolDto>> result = JSON.parseObject(responseBody, new TypeReference<>() {});
            if (result.getCode() == 200) {
                List<ToolDto> data = result.getData();

                // 处理工具id标识问题
                for(ToolDto tool : data){
                    if(tool.getToolInfo() != null){
                        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(tool.getToolInfo()) ;
                        jsonObject.put("id" , tool.getId()) ;
                        tool.setToolInfo(jsonObject.toJSONString());
                    }
                }

                return data ;
            }
        }

        return Collections.emptyList();
    }

    /**
     * 查询数据为以下条件：
     * 1. 当前组织orgId的数据而且getToolPermission为ORG的数据
     * 2. getToolPermission 为PUBLIC的数据
     * 3. 当前getToolPermission为 PERSON 但是数据为当前operatorId的数据
     */
    @Override
    public TableDataInfo toolSelection(DatatablesPageBean page, PermissionQuery query) {

        int pageNow = page.getPageNum();
        int pageSize = page.getPageSize();

        Long orgId = query.getOrgId() ;
        Long operatorId = query.getOperatorId() ;

        LambdaQueryWrapper<ToolEntity> wrapper = new LambdaQueryWrapper<>();

        // 补充查询条件
        if (orgId != null) {
            // 条件1: 当前组织orgId的数据而且getToolPermission为ORG的数据
            // 条件2: getToolPermission为PUBLIC的数据
            wrapper.and(w -> w
                    .eq(ToolEntity::getOrgId, orgId)
                    .eq(ToolEntity::getToolPermission, "ORG")
                    .or()
                    .eq(ToolEntity::getToolPermission, "PUBLIC")
            );

            // 条件3: 当前getToolPermission为PERSON但是数据为当前operatorId的数据
            if (operatorId != null) {
                wrapper.or(w -> w
                        .eq(ToolEntity::getToolPermission, "PERSON")
                        .eq(ToolEntity::getFromId, operatorId) // 假设fromId存储的是创建者ID
                );
            }
        } else {
            // 如果orgId为空，则只查询公共工具和个人工具
            wrapper.eq(ToolEntity::getToolPermission, "PUBLIC");

            // 如果operatorId不为空，还需要查询该用户的个人工具
            if (operatorId != null) {
                wrapper.or()
                        .eq(ToolEntity::getToolPermission, "PERSON")
                        .eq(ToolEntity::getFromId, operatorId); // 假设fromId存储的是创建者ID
            }
        }

        Page<ToolEntity> pageResult = new Page<>(pageNow, pageSize) ;
        pageResult = this.page(pageResult, wrapper);

        // 获取到MCP工具列表
        List<ToolEntity> mcpTools = getMcpToolList(orgId) ;
        if(!CollectionUtils.isEmpty(mcpTools)){
            List<ToolEntity> list =  pageResult.getRecords() ;

            // 设置list名称
            if(list != null && !list.isEmpty()){
                list.forEach(tool -> tool.setName("(STDIO) "+tool.getName()));
                list.addAll(mcpTools) ;
            }else{
                list = mcpTools ;
            }

            pageResult.setRecords(list) ;
            pageResult.setTotal(pageResult.getTotal() + mcpTools.size()) ;
        }

        TableDataInfo tableDataInfo = new TableDataInfo();

        tableDataInfo.setRows(pageResult.getRecords());
        tableDataInfo.setTotal(pageResult.getTotal()) ;

        return tableDataInfo;
    }

    /**
     * 获取MCP工具列表
     * @return
     */
    private List<ToolEntity> getMcpToolList(Long orgId) {
        McpConfigEntity mcpConfigEntity = mcpConfigService.getByOrgId(orgId) ;
        if(mcpConfigEntity == null){
            return Collections.emptyList();
        }

        // 网络请求到MCP工具列表
        String baseUrl = mcpConfigEntity.getMcpUrl().trim();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        String url = baseUrl + "mcp/getAllTool?orgId=" + orgId;

        // 发送HTTP请求
        String response = HttpUtil.get(url).trim() ;

        // 将返回结果转换成R<String>
        R<List<ToolEntity>> r = JSON.parseObject(response, new TypeReference<>() {});
        List<ToolEntity> list = r.getData();
        if (list != null) {
            for (ToolEntity toolEntity : list) {
                toolEntity.setName("(MCP) "+toolEntity.getName()) ;
                toolEntity.setToolCatalog("mcp");
            }
        }
        return list;

    }

    /**
     * 执行MCP工具类
     *
     * @param toolId
     * @param argsList
     * @param orgId
     * @return
     */
    @Override
    public ToolResult executeMcpTool(String toolId, Map<String, String> argsList, Long orgId) {
        McpConfigEntity mcpConfigEntity = mcpConfigService.getByOrgId(orgId) ;

        ToolResult toolResult = new ToolResult();
        toolResult.setFinished(false);

        try {
            // 获取MCP配置
            if (mcpConfigEntity == null) {
                toolResult.setOutput("MCP配置未找到");
                return toolResult;
            }

            // 构建请求URL
            String baseUrl = mcpConfigEntity.getMcpUrl().trim();
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            String url = baseUrl + "mcp/executeTool?toolId=" + toolId;

            // 构建请求体JSON
            JSONObject jsonBody = new JSONObject();
            jsonBody.set("toolId", toolId);
            jsonBody.set("params", JSONUtil.parseObj(argsList));

            // 发送HTTP请求
            HttpResponse response = HttpUtil.createPost(url)
                    .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                    .header(Header.ACCEPT, "*/*")
                    .body(jsonBody.toString())
                    .execute();

            // 处理响应
            if (response.isOk()) {
                JSONObject jsonResponse = JSONUtil.parseObj(response.body());
                toolResult.setOutput(jsonResponse.get("data"));
            } else {
                toolResult.setOutput("请求失败，状态码：" + response.getStatus());
            }
        } catch (Exception e) {
            toolResult.setOutput("执行MCP工具失败: " + e.getMessage());
            log.error("MCP工具执行失败", e);
        }

        return toolResult ;
    }
}