package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
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

import java.util.List;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class IndustryRoleServiceImpl extends IBaseServiceImpl<IndustryRoleEntity, IndustryRoleMapper> implements IIndustryRoleService {


    @Autowired
    private IWorkflowExecutionService workflowExecutionService ;

    private static final Gson gson = new Gson() ;

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

        IndustryRoleEntity role = new IndustryRoleEntity() ;
        role.setId(IdUtil.getSnowflakeNextId());
        role.setRoleName("AIP智能体小助理");
        role.setRoleAvatar("1830185154541305857");

        return role;
    }

    @Override
    public void updatePromptContent(List<PromptMessageDto> messageDto, String roleId) {
        IndustryRoleEntity e = getById(roleId) ;
        e.setPromptContent(gson.toJson(messageDto));
        this.update(e) ;
    }

    @Override
    public WorkflowExecutionDto runRoleAgent(MessageTaskInfo taskInfo) {

        long roleId = taskInfo.getRoleId() ;
        IndustryRoleEntity role = getById(roleId) ;

        // 获取到节点的执行内容信息
        String businessIds = taskInfo.getPreBusinessId() ;
        WorkflowExecutionEntity workflowExecutionEntity = workflowExecutionService.getById(businessIds) ;

        IBaseExpertService baseExpertService = (IBaseExpertService) SpringContext.getBean(role.getChainId());

        return baseExpertService.runRoleAgent(role , workflowExecutionEntity , taskInfo);
    }

}