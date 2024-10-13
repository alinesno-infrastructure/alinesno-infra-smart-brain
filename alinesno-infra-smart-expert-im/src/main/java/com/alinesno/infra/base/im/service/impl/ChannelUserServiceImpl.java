package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.mapper.ChannelRoleMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.ChannelAgentDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.entity.ChannelRoleEntity;
import com.alinesno.infra.smart.im.service.IChannelRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

    @Override
    public void updateChannelAgent(ChannelAgentDto dto) {
        long channelId = dto.getChannelId() ;
        List<Long> roleIds = dto.getRolesId() ;

        // 删除之前频道的角色列表
        LambdaUpdateWrapper<ChannelRoleEntity> wrapper = new LambdaUpdateWrapper<>() ;
        wrapper.eq(ChannelRoleEntity::getChannelId , channelId) ;
        remove(wrapper);

        // 新增加频道列表
        List<ChannelRoleEntity> entities = new ArrayList<>() ;
        for (long roleId : roleIds) {
            ChannelRoleEntity entity = new ChannelRoleEntity() ;
            entity.setChannelId(channelId);
            entity.setAccountId(roleId);
            entity.setAccountType("agent");
            entities.add(entity);
        }

        saveBatch(entities);
    }

}
