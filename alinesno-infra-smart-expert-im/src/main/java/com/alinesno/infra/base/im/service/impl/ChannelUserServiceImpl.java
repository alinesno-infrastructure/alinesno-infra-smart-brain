package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.mapper.ChannelRoleMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.entity.ChannelRoleEntity;
import com.alinesno.infra.smart.im.service.IChannelRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ChannelUserServiceImpl extends IBaseServiceImpl<ChannelRoleEntity, ChannelRoleMapper> implements IChannelRoleService {

    @Autowired
    private IIndustryRoleService roleService;

    @Override
    public List<IndustryRoleEntity> getChannelAgent(String channelId) {

        LambdaQueryWrapper<ChannelRoleEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(ChannelRoleEntity::getChannelId , channelId)
               .eq(ChannelRoleEntity::getAccountType , "agent") ;

        List<ChannelRoleEntity> userEntities = list(wrapper) ;
        List<Long> agentIds = userEntities.stream()
                .map(ChannelRoleEntity::getAccountId)
                .toList();

        log.info("agentIds:{}" , agentIds) ;
        if(agentIds.isEmpty()){
            return new ArrayList<>() ;
        }

        return roleService.listByIds(agentIds);
    }

}
