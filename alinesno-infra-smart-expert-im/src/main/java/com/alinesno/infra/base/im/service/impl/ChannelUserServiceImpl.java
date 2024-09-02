package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.entity.ChannelUserEntity;
import com.alinesno.infra.base.im.entity.UserEntity;
import com.alinesno.infra.base.im.mapper.ChannelUserMapper;
import com.alinesno.infra.base.im.service.IChannelUserService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ChannelUserServiceImpl extends IBaseServiceImpl<ChannelUserEntity, ChannelUserMapper> implements IChannelUserService {

    @Autowired
    private IIndustryRoleService roleService;

    @Override
    public List<UserEntity> getChannelAgent(String channelId) {

        LambdaQueryWrapper<ChannelUserEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(ChannelUserEntity::getChannelId , channelId)
               .eq(ChannelUserEntity::getAccountType , "agent") ;

        List<ChannelUserEntity> userEntities = list(wrapper) ;
        List<Long> agentIds = userEntities.stream()
                .map(ChannelUserEntity::getAccountId)
                .toList();

        log.info("agentIds:{}" , agentIds) ;
        if(agentIds.isEmpty()){
            return new ArrayList<>() ;
        }

        List<IndustryRoleEntity> list = roleService.listByIds(agentIds) ; // smartAssistantConsumer.getAgentListByIds(agentIds) ;
        List<UserEntity> userList = new ArrayList<>() ;

        for(IndustryRoleEntity dto : list){

            UserEntity e =  new UserEntity() ;
            BeanUtils.copyProperties(dto, e);
            e.setAvatar(dto.getRoleAvatar());

            userList.add(e) ;
        }

        return userList ;
    }
}
