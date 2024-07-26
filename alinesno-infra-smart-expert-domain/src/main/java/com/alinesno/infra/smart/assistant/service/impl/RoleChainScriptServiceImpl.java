package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.chain.IChainService;
import com.alinesno.infra.smart.assistant.entity.RoleChainScriptEntity;
import com.alinesno.infra.smart.assistant.mapper.RoleChainScriptMapper;
import com.alinesno.infra.smart.assistant.redis.MessageConstants;
import com.alinesno.infra.smart.assistant.redis.PublishRedisService;
import com.alinesno.infra.smart.assistant.service.IRoleChainScriptService;
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
public class RoleChainScriptServiceImpl extends IBaseServiceImpl<RoleChainScriptEntity, RoleChainScriptMapper> implements IRoleChainScriptService {

    @Autowired
    private IChainService chainService ;

    @Autowired
    private PublishRedisService publishService ;

    @Override
    public void saveScript(RoleChainScriptEntity entity) {
        this.save(entity) ;

        // 验证脚本是否正确
        chainService.checkScript(entity) ;

        // 发送消息用于规则的热更新
        publishService.sendMsg(MessageConstants.RELOAD_RULE);
    }

    @Override
    public void deleteScript(String ids) {

        String[] rowsId = ids.split(",");
        Long[] longIds = new Long[rowsId.length];

        for(int i = 0; i < rowsId.length; ++i) {
            longIds[i] = Long.parseLong(rowsId[i]);
        }

        if (rowsId.length > 0) {
            this.deleteByIds(longIds);
        }

        // 发送消息用于规则的热更新
        publishService.sendMsg(MessageConstants.RELOAD_RULE);
    }

    @Override
    public void updateScript(RoleChainScriptEntity entity) {
        this.update(entity);

        // 验证脚本是否正确
        chainService.checkScript(entity) ;

        // 发送消息用于规则的热更新
        publishService.sendMsg(MessageConstants.RELOAD_RULE);
    }
}