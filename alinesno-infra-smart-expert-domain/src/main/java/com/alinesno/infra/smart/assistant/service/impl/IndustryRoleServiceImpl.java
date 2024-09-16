package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.mapper.IndustryRoleMapper;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IWorkflowExecutionService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 应用构建Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class IndustryRoleServiceImpl extends IBaseServiceImpl<IndustryRoleEntity, IndustryRoleMapper> implements IIndustryRoleService {

    @Autowired
    private IWorkflowExecutionService workflowExecutionService;

    @Autowired
    private BaseSearchConsumer baseSearchConsumer; ;

    private static final Gson gson = new Gson();

    @Override
    public List<IndustryRoleEntity> getNewestRole() {

        LambdaQueryWrapper<IndustryRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(IndustryRoleEntity::getAddTime);
        queryWrapper.last("limit 8");

        return this.list(queryWrapper);
    }

    @Override
    public List<IndustryRoleEntity> getRoleByUserName(List<String> users) {

        LambdaQueryWrapper<IndustryRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(IndustryRoleEntity::getRoleName, users);

        if (list(queryWrapper) != null) {
            return list(queryWrapper);
        }

        return null;
    }

    @Override
    public IndustryRoleEntity getDefaultHelpAgent() {

        log.info("getDefaultHelpAgent");

        IndustryRoleEntity role = new IndustryRoleEntity();

        role.setId(IdUtil.getSnowflakeNextId());
        role.setRoleName("AIP智能体小助理");
        role.setRoleAvatar("1830185154541305857");

        return role ;
    }

    @Override
    public void updatePromptContent(List<PromptMessageDto> messageDto, String roleId) {
        IndustryRoleEntity e = getById(roleId);
        e.setPromptContent(gson.toJson(messageDto));
        this.update(e);
    }

    @Override
    public WorkflowExecutionDto runRoleAgent(MessageTaskInfo taskInfo) {

        long roleId = taskInfo.getRoleId();
        IndustryRoleEntity role = getById(roleId);

        // 获取到节点的执行内容信息
        WorkflowExecutionEntity workflowExecutionEntity = null;

        String preBusinessId = taskInfo.getPreBusinessId();  // 获取到前一个节点的业务ID
        log.info("preBusinessId:{}", preBusinessId);

        if (StringUtils.hasLength(preBusinessId)) {
            workflowExecutionEntity = workflowExecutionService.getById(preBusinessId);
        }

        log.debug("role.getChainId() = {}", role.getChainId());
        IBaseExpertService expertService = getiBaseExpertService(role);

        return expertService.runRoleAgent(role, workflowExecutionEntity, taskInfo);
    }

    @Override
    public void batchCreateRole(List<IndustryRoleEntity> allEntities) {

        // 判断是否为空，不为空则输出warning
        if (allEntities.isEmpty()) {
            log.warn("传入的实体列表为空，无法创建角色");
            return;
        }

        // 创建角色知识库
        for (IndustryRoleEntity role : allEntities) {
            // TODO 待集成批量添加知识库
            R<String> result = baseSearchConsumer.datasetCreate(role.getResponsibilities() , role.getRoleName());
            log.debug("创建知识库结果：" + result);
            role.setKnowledgeId(result.getData());
        }

        // 先保存用户信息
        this.saveOrUpdateBatch(allEntities);
    }

    /**
     * 根据行业角色获取专家服务
     *
     * @param role 行业角色实体对象，包含了链ID等信息，用于定位特定的专家服务实例
     * @return 返回对应的专家服务接口实现对象如果无法获取到合适的实现对象，将返回默认角色的专家服务
     */
    private static IBaseExpertService getiBaseExpertService(IndustryRoleEntity role) {
        IBaseExpertService expertService = null;

        // 从Spring上下文中根据链ID获取Bean
        try{
            expertService = (IBaseExpertService) SpringContext.getBean(role.getChainId());
        }catch (Exception e){
            log.error("无法获取到指定的专家服务实例，使用默认角色", e);
            expertService = (IBaseExpertService) SpringContext.getBean(AssistantConstants.PREFIX_ASSISTANT_DEFAULT); // 使用默认角色
        }

        return expertService;
    }

}