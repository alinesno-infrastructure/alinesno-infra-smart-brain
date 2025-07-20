package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.api.*;
import com.alinesno.infra.smart.assistant.api.config.RoleFlowConfigDto;
import com.alinesno.infra.smart.assistant.api.config.RoleReActConfigDto;
import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleCatalogEntity;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleDraftEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.enums.ScriptPurposeEnums;
import com.alinesno.infra.smart.assistant.mapper.IndustryRoleCatalogMapper;
import com.alinesno.infra.smart.assistant.mapper.IndustryRoleDraftMapper;
import com.alinesno.infra.smart.assistant.service.*;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 应用构建Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class IndustryRoleDraftServiceImpl extends IBaseServiceImpl<IndustryRoleDraftEntity, IndustryRoleDraftMapper> implements IIndustryRoleDraftService {

    @Autowired
    private IWorkflowExecutionService workflowExecutionService;

    @Autowired
    private IndustryRoleCatalogMapper roleCatalogMapper ;

    @Autowired
    private BaseSearchConsumer baseSearchConsumer; ;

    @Autowired
    private IRolePushOrgService rolePushOrgService ;

    @Autowired
    private IToolService toolService ;

    @Autowired
    private ThreadPoolTaskExecutor chatThreadPool;

    private static final Gson gson = new Gson();

    @Override
    public List<IndustryRoleDraftEntity> getNewestRole(PermissionQuery query) {

        LambdaQueryWrapper<IndustryRoleDraftEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(IndustryRoleDraftEntity.class) ;
        query.toWrapper(queryWrapper);

        queryWrapper.orderByDesc(IndustryRoleDraftEntity::getAddTime);
        queryWrapper.last("limit 8");

        return this.list(queryWrapper);
    }

    @Override
    public List<IndustryRoleDraftEntity> getRoleByUserName(List<String> users) {

        LambdaQueryWrapper<IndustryRoleDraftEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(IndustryRoleDraftEntity::getRoleName, users);

        if (list(queryWrapper) != null) {
            return list(queryWrapper);
        }

        return null;
    }

    @Override
    public IndustryRoleDraftEntity getDefaultHelpAgent() {

        log.info("getDefaultHelpAgent");

        IndustryRoleDraftEntity role = new IndustryRoleDraftEntity();

        role.setId(IdUtil.getSnowflakeNextId());
        role.setRoleName("AIP智能体小助理");
        role.setRoleAvatar("1830185154541305857");

        return role ;
    }

    @Override
    public void updatePromptContent(List<PromptMessageDto> messageDto, String roleId) {
        IndustryRoleDraftEntity e = getById(roleId);
        e.setPromptContent(gson.toJson(messageDto));
        this.update(e);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public CompletableFuture<WorkflowExecutionDto> runRoleAgent(MessageTaskInfo taskInfo) {
        long roleId = taskInfo.getRoleId();
        IndustryRoleDraftEntity role = getById(roleId);
        Assert.notNull(role, "角色不存在");

        // 同步更新角色会话次数
        role.setChatCount(role.getChatCount() == null ? 0L : role.getChatCount() + 1);
        update(role);

        taskInfo.setRoleDto(role);

        // 获取到节点的执行内容信息
        String preBusinessId = taskInfo.getPreBusinessId();  // 获取到前一个节点的业务ID

        // 构建异步执行链
        return CompletableFuture.supplyAsync(() -> {
                    MessageEntity message = null;
                    if (StringUtils.hasLength(preBusinessId)) {
                        IMessageService messageService = SpringUtils.getBean(IMessageService.class);
                        message = messageService.getById(preBusinessId);
                    }
                    return message;
                }, chatThreadPool) // 使用专用线程池
                .thenCompose(message -> {
                    IBaseExpertService expertService = getiBaseExpertService(role.getChainId());
                    // 假设expertService.runRoleAgent已改为异步方法
                    return expertService.runRoleAgent(role, message, taskInfo);
                })
                .thenApply(dto -> {
                    workflowExecutionService.saveRecord(dto);
                    return dto;
                })
                .exceptionally(ex -> {
                    log.error("执行角色代理失败", ex);
                    // 可以在这里处理异常情况，返回默认值或抛出特定异常
                    throw new RuntimeException("执行角色代理失败", ex);
                });
    }

    @Override
    public void batchCreateRole(List<IndustryRoleDraftEntity> allEntities) {

        // 判断是否为空，不为空则输出warning
        if (allEntities.isEmpty()) {
            log.warn("传入的实体列表为空，无法创建角色");
            return;
        }

        // 创建角色知识库
//        for (IndustryRoleDraftEntity role : allEntities) {
//            // TODO 待集成批量添加知识库
//            R<Long> result = baseSearchConsumer.datasetCreate(
//                    role.getResponsibilities() ,
//                    role.getRoleName() ,
//                    role.getOrgId() + "" ,
//                    role.getOperatorId() + "");
//
//            log.debug("创建知识库结果：" + result);
//            role.setKnowledgeId(result.getData() + "");
//        }

        // 先保存用户信息
        this.saveOrUpdateBatch(allEntities);
    }

    /**
     * 根据行业角色获取专家服务
     *
     * @param chainId 行业角色实体对象，包含了链ID等信息，用于定位特定的专家服务实例
     * @return 返回对应的专家服务接口实现对象如果无法获取到合适的实现对象，将返回默认角色的专家服务
     */
    private static IBaseExpertService getiBaseExpertService(String chainId) {
        IBaseExpertService expertService = null;

        // 从Spring上下文中根据链ID获取Bean
        try{
            expertService = (IBaseExpertService) SpringContext.getBean(chainId);
        }catch (Exception e){
            log.error("无法获取到指定的专家服务实例，使用默认角色", e);
            expertService = (IBaseExpertService) SpringContext.getBean(AssistantConstants.PREFIX_ASSISTANT_DEFAULT); // 使用默认角色
        }

        return expertService;
    }

    @Override
    public void updateRoleScript(RoleScriptDto dto) {

        IndustryRoleDraftEntity role = getById(dto.getRoleId());

        setRoleConfigParams(dto.getAgentConfigParams() , role);

        if(dto.getType().equals(ScriptPurposeEnums.EXECUTE.getValue())){  // 执行脚本
            log.info("执行脚本：{}", dto.getScript());
            role.setExecuteScript(dto.getScript());
        } else if(dto.getType().equals(ScriptPurposeEnums.AUDIT.getValue())){  // 审核脚本
            log.info("审核脚本：{}", dto.getScript());
            role.setAuditScript(dto.getScript());
        } else if(dto.getType().equals(ScriptPurposeEnums.FUNCTION_CALL.getValue())){  // 功能回调脚本
            log.info("功能回调脚本：{}", dto.getScript());
            role.setFunctionCallbackScript(dto.getScript());
        }

        // 只要保存有脚本，则设置成流程脚本
        role.setChainId(AssistantConstants.PREFIX_ASSISTANT_SCRIPT);

        this.update(role);
    }

    @Override
    public CompletableFuture<WorkflowExecutionDto> validateRoleScript(RoleScriptDto dto) {
        log.debug("validateRoleScript:{}" , dto);

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;
        taskInfo.setRoleId(dto.getRoleId());
        taskInfo.setText(dto.getMessage());

        long roleId = taskInfo.getRoleId();
        IndustryRoleDraftEntity role = getById(roleId);
        role.setChainId(AssistantConstants.PREFIX_ASSISTANT_SCRIPT);

        if(dto.getType().equals(ScriptPurposeEnums.EXECUTE.getValue())){  // 执行脚本
            log.info("执行脚本：{}", dto.getScript());
            taskInfo.setFunctionCall(false);
            taskInfo.setModify(false);
            role.setExecuteScript(dto.getScript());
        } else if(dto.getType().equals(ScriptPurposeEnums.AUDIT.getValue())){  // 审核脚本
            log.info("审核脚本：{}", dto.getScript());
            taskInfo.setModify(true);
            role.setAuditScript(dto.getScript());
        } else if(dto.getType().equals(ScriptPurposeEnums.FUNCTION_CALL.getValue())){ // 功能回调脚本
            role.setFunctionCallbackScript(dto.getScript());
            taskInfo.setFunctionCall(true);
        }

        // 获取到节点的执行内容信息
        MessageEntity message = new MessageEntity() ;
        String preGenContent = """
                    ```json
                    {"name":"测试脚本"}
                    ```
                """ ;
        message.setContent(preGenContent);

        log.debug("role.getChainId() = {}", role.getChainId());
        IBaseExpertService expertService = getiBaseExpertService(role.getChainId());

        taskInfo.setRoleDto(role);

        return expertService.runRoleAgent(role, message, taskInfo);
    }

    @Override
    public void createRole(IndustryRoleDraftEntity e) {

        switch (e.getScriptType()) {
            case "script" -> e.setChainId(AssistantConstants.PREFIX_ASSISTANT_SCRIPT);
            case "react" -> e.setChainId(AssistantConstants.PREFIX_ASSISTANT_REACT);
            case "flow" -> e.setChainId(AssistantConstants.PREFIX_ASSISTANT_FLOW);
        }

        List<IndustryRoleDraftEntity> allEntities = new ArrayList<>();
        allEntities.add(e);

        batchCreateRole(allEntities);
    }

    @Override
    public void recommended(long roleId , long orgId) {

        // 设置当前频道所有其它角色为不推荐状态
        LambdaUpdateWrapper<IndustryRoleDraftEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(IndustryRoleDraftEntity::getOrgId, orgId);
        lambdaUpdateWrapper.set(IndustryRoleDraftEntity::isHasRecommended, false) ;
        update(lambdaUpdateWrapper);

        // 设置当前角色为更新状态
        IndustryRoleDraftEntity role = getById(roleId);
        role.setHasRecommended(true);
        this.update(role);
    }

    @Override
    public IndustryRoleDraftEntity getRecommendRole(long orgId) {

        LambdaQueryWrapper<IndustryRoleDraftEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(IndustryRoleDraftEntity::getOrgId, orgId);
        lambdaQueryWrapper.eq(IndustryRoleDraftEntity::isHasRecommended, true);

        List<IndustryRoleDraftEntity> list = list(lambdaQueryWrapper);

        if(!list.isEmpty()){
            return list.get(0);
        }

        return null;
    }

    @Override
    public void saveRoleWithTool(RoleToolRequestDTO dto) {
        IndustryRoleDraftEntity role = getById(dto.getRoleId()) ;
        role.setGreeting(dto.getGreeting());
        role.setBackstory(dto.getBackstory());
        role.setChainId(AssistantConstants.PREFIX_ASSISTANT_REACT);
        role.setAskHumanHelp(dto.isAskHumanHelp());
        update(role) ;

        // TODO 待处理更新角色工具
//        boolean b = roleToolService.updateRoleTools(dto.getRoleId(), dto.getTools()) ;
//        log.debug("updateRoleTools = {}", b);
    }

    @Override
    public CompletableFuture<WorkflowExecutionDto> validateReActRole(ReActRoleScriptDto dto) {

        log.debug("validateReActRole:{}" , dto);

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;
        taskInfo.setRoleId(dto.getRoleId());
        taskInfo.setText(dto.getMessage());

        long roleId = taskInfo.getRoleId();
        IndustryRoleDraftEntity role = getById(roleId);
        role.setChainId(AssistantConstants.PREFIX_ASSISTANT_REACT);

        // 获取到节点的执行内容信息
        MessageEntity message = new MessageEntity() ;
        String preGenContent = """
                    ```json
                    {"name":"测试脚本"}
                    ```
                """ ;
        message.setContent(preGenContent);

        log.debug("role.getChainId() = {}", role.getChainId());
        IBaseExpertService expertService = getiBaseExpertService(role.getChainId());
        taskInfo.setRoleDto(role);

        return expertService.runRoleAgent(role, message, taskInfo);
    }

    /**
     * 添加角色到指定的团队里面，类似于办理入职手续过程。
     *
     * @param roleId
     * @param orgId
     * @param userId
     * @param deptId
     * @param isPush
     */
    @Override
    public void employRole(long roleId, long orgId , long userId , long deptId, boolean isPush) {

        // 如果是推送录用，则删除之前角色关联信息，重新添加
        if(isPush){
            clearPushRole(roleId, orgId);
        }

        IIndustryRoleCatalogService industryRoleCatalogService = SpringUtils.getBean(IIndustryRoleCatalogService.class);
        IndustryRoleCatalogEntity entity = new IndustryRoleCatalogEntity() ;
        entity.setOrgId(orgId);
        entity.setOperatorId(userId) ;
        entity.setDepartmentId(deptId);
        IndustryRoleCatalogEntity defaultCatalog = industryRoleCatalogService.getDefaultCatalog(entity);

        // 判断当前用户是否已经买过这个角色
        LambdaQueryWrapper<IndustryRoleDraftEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(IndustryRoleDraftEntity::getOrgId, orgId);
        lambdaQueryWrapper.eq(IndustryRoleDraftEntity::getSaleFromRoleId, roleId);

        long count = count(lambdaQueryWrapper);
        Assert.isTrue(count == 0 , "您所在组织已经购买过该角色，请勿重复购买！");

        IndustryRoleDraftEntity role = getById(roleId) ;

        if(role.getOrgId() == orgId){
            throw new RpcServiceRuntimeException("您不能录用自己所属的组织的角色！");
        }

        // 录用角色
        role.setOperatorId(userId);
        role.setOrgId(orgId);
        role.setDepartmentId(deptId);
        role.setId(null);
        role.setHasRecommended(false); // 默认不推荐
        role.setHasSale(9); // 不允许转售

        role.setSaleFromRoleId(roleId);
        role.setIndustryCatalog(defaultCatalog.getId());  // 添加到默认团队中

        save(role);

        // 复制工具
        copyRoleTool(roleId, orgId, userId, deptId, role);

        // TODO 知识库的复制(待完善) 确定是否复制?

       if(isPush) {  // 如果为组织单独推送，则标识角色为已录用
           rolePushOrgService.acceptOrgRole(roleId, orgId);
       }

    }

    @Override
    public void saveRoleWithReActConfig(RoleReActConfigDto dto) {
        IndustryRoleDraftEntity role = getById(dto.getRoleId());
        setRoleConfigParams(dto, role);

        update(role);
    }

    @Override
    public void modifyInfo(RoleInfoDto dto) {
        // 修改角色信息
        IndustryRoleDraftEntity role = getById(dto.getId());

        role.setRoleAvatar(dto.getRoleAvatar());
        role.setRoleName(dto.getRoleName());
        role.setIndustryCatalog(dto.getIndustryCatalog());
        role.setResponsibilities(dto.getResponsibilities());

        switch (dto.getScriptType()) {
            case "script" -> role.setChainId(AssistantConstants.PREFIX_ASSISTANT_SCRIPT);
            case "react" -> role.setChainId(AssistantConstants.PREFIX_ASSISTANT_REACT);
            case "flow" -> role.setChainId(AssistantConstants.PREFIX_ASSISTANT_FLOW);
        }

        role.setScriptType(dto.getScriptType());

        update(role) ;
    }

    @Override
    public void updateFlowConfig(RoleFlowConfigDto dto, Long roleId) {
        IndustryRoleDraftEntity role = getById(roleId);

        // 更新角色的配置信息
        role.setGreeting(dto.getGreeting());
        role.setVoiceInputStatus(dto.isVoiceInputStatus());
        role.setGuessWhatYouAskStatus(dto.isGuessWhatYouAskStatus());
        role.setLongTermMemoryEnabled(dto.isLongTermMemoryEnabled());
        role.setVoicePlayStatus(dto.isVoicePlayStatus());

        // 语音输入设置
        if(dto.getVoiceInputData() != null){
            role.setVoiceInputData(JSONObject.toJSONString(dto.getVoiceInputData()));
        }

        // 语音播放设置
        if(dto.getVoicePlayData() != null){
            role.setVoicePlayData(JSONObject.toJSONString(dto.getVoicePlayData()));
        }

        // 猜你想问设置
        if(dto.getGuessWhatYouAskData() != null){
            role.setGuessWhatYouAskData(JSONObject.toJSONString(dto.getGuessWhatYouAskData()));
        }
        update(role);
    }

    private void setRoleConfigParams(RoleReActConfigDto dto, IndustryRoleDraftEntity role) {
        role.setModelId(dto.getModelId());
        role.setPromptContent(dto.getPromptContent());
        role.setGreeting(dto.getGreeting());
        role.setVoiceInputStatus(dto.isVoiceInputStatus());
        role.setGuessWhatYouAskStatus(dto.isGuessWhatYouAskStatus());
        role.setLongTermMemoryEnabled(dto.isLongTermMemoryEnabled());
        role.setVoicePlayStatus(dto.isVoicePlayStatus());
        role.setUploadStatus(dto.isUploadStatus());

        if(dto.getUploadData() != null){
            role.setUploadData(JSONObject.toJSONString(dto.getUploadData()));
        }

        if(dto.getModelConfig() != null){
            role.setModelConfig(JSONObject.toJSONString(dto.getModelConfig()));
        }

        if(CollectionUtils.isNotEmpty(dto.getKnowledgeBaseIds())){
            role.setKnowledgeBaseIds(JSONObject.toJSONString(dto.getKnowledgeBaseIds()));
        }

        if(CollectionUtils.isNotEmpty(dto.getSelectionToolsData())){
            role.setSelectionToolsData(JSONObject.toJSONString(dto.getSelectionToolsData()));
        }

        if(dto.getVoiceInputData() != null){
            role.setVoiceInputData(JSONObject.toJSONString(dto.getVoiceInputData()));
        }

        // 数据集配置
        if(dto.getDatasetSearchConfig() != null){
            role.setDatasetSearchConfig(JSONObject.toJSONString(dto.getDatasetSearchConfig()));
        }

        if(dto.getGuessWhatYouAskData() != null){
            role.setGuessWhatYouAskData(JSONObject.toJSONString(dto.getGuessWhatYouAskData()));
        }

        // 语音播放设置
        if(dto.getVoicePlayData() != null){
            role.setVoicePlayData(JSONObject.toJSONString(dto.getVoicePlayData()));
        }
    }

    /**
     * 删除所有角色关联信息，包括角色信息，还有工具信息
     * @param roleId
     * @param orgId
     */
    private void clearPushRole(long roleId, long orgId) {

        LambdaQueryWrapper<IndustryRoleDraftEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(IndustryRoleDraftEntity::getOrgId, orgId);
        lambdaQueryWrapper.eq(IndustryRoleDraftEntity::getSaleFromRoleId, roleId);
        remove(lambdaQueryWrapper);

        // TODO 删除工具信息
//        IndustryRoleDraftEntity roleEntity = getOne(lambdaQueryWrapper) ;
//        if(roleEntity != null){
//            roleToolService.remove(new LambdaQueryWrapper<RoleToolEntity>().eq(RoleToolEntity::getRoleId, roleEntity.getId()));
//        }
    }

    /**
     * TODO 复制工具
     * @param roleId
     * @param orgId
     * @param userId
     * @param deptId
     * @param role
     */
    private void copyRoleTool(long roleId, long orgId, long userId, long deptId, IndustryRoleDraftEntity role) {

//        // 插件工具的复制(待完善)，思路先复制工具，再处理工具关系
//        LambdaUpdateWrapper<RoleToolEntity>  toolLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        toolLambdaUpdateWrapper.eq(RoleToolEntity::getRoleId, roleId);
//
//        if(roleToolService.count(toolLambdaUpdateWrapper) > 0){
//            List<RoleToolEntity> toolEntities = roleToolService.list(toolLambdaUpdateWrapper);
//
//            // 查询出所有涉及到的工具
//            List<Long> toolIds = toolEntities.stream().map(RoleToolEntity::getToolId).toList();
//
//            if(CollectionUtils.isNotEmpty(toolIds)){
//
//                LambdaQueryWrapper<ToolEntity> toolLambdaQueryWrapper = new LambdaQueryWrapper<>();
//                toolLambdaQueryWrapper.in(ToolEntity::getId, toolIds);
//
//                List<ToolEntity> toolEntities1 = toolService.list(toolLambdaQueryWrapper);
//                List<ToolEntity> copyToolEntities = new ArrayList<>() ;
//
//                if(CollectionUtils.isNotEmpty(toolEntities1)){
//                    for (ToolEntity toolEntity : toolEntities1) {
//
//                        ToolEntity toolEntity1  ;
//
//                        // 先通过id查询是否当前组织已经拥有此工具
//                        LambdaQueryWrapper<ToolEntity> toolLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
//                        toolLambdaQueryWrapper1.eq(ToolEntity::getOrgId, orgId);
//                        toolLambdaQueryWrapper1.eq(ToolEntity::getFromId, toolEntity.getId());
//
//                        if(toolService.count(toolLambdaQueryWrapper1) > 0){
//                            toolEntity1 = toolService.getOne(toolLambdaQueryWrapper1);
//                        }else{
//                            toolEntity1 = new ToolEntity();
//                            BeanUtils.copyProperties(toolEntity, toolEntity1);
//
//                            toolEntity1.setId(null);
//                            toolEntity1.setOrgId(orgId);
//                            toolEntity1.setOperatorId(userId);
//                            toolEntity1.setDepartmentId(deptId);
//                            toolEntity1.setFromId(toolEntity.getId()); // 来源工具
//
//                            // 保存工具
//                            toolService.save(toolEntity1) ;
//                        }
//
//                        copyToolEntities.add(toolEntity1);
//                    }
//                }
//
//                // 将用户与工具关联一起
//                List<RoleToolEntity> copyRoleTools = new ArrayList<>() ;
//                for(ToolEntity toolEntity : copyToolEntities){
//                    RoleToolEntity roleToolEntity = new RoleToolEntity();
//                    roleToolEntity.setRoleId(role.getId());
//                    roleToolEntity.setToolId(toolEntity.getId());
//
//                    copyRoleTools.add(roleToolEntity);
//                }
//                roleToolService.saveBatch(copyRoleTools);
//            }
//
//        }
    }

}