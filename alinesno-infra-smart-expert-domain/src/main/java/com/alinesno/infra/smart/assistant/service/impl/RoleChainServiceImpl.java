package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.chain.IChainService;
import com.alinesno.infra.smart.assistant.entity.RoleChainEntity;
import com.alinesno.infra.smart.assistant.mapper.RoleChainMapper;
import com.alinesno.infra.smart.assistant.redis.MessageConstants;
import com.alinesno.infra.smart.assistant.redis.PublishRedisService;
import com.alinesno.infra.smart.assistant.service.IRoleChainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class RoleChainServiceImpl extends IBaseServiceImpl<RoleChainEntity, RoleChainMapper> implements IRoleChainService {

    @Autowired
    private PublishRedisService publishService ;

    @Autowired
    private IChainService chainService ;

    @Override
    public void runById(Long chainId) {

        RoleChainEntity roleChain = getById(chainId) ;

        chainService.executeRule(roleChain) ;
    }

    @Override
    public void saveRoleChain(RoleChainEntity entity) {
        this.save(entity) ;

        // 发送消息用于规则的热更新
        publishService.sendMsg(MessageConstants.RELOAD_RULE);
    }

    @Override
    public void updateRoleChain(RoleChainEntity entity) {
        this.update(entity) ;

        // 发送消息用于规则的热更新
        publishService.sendMsg(MessageConstants.RELOAD_RULE);
    }
}