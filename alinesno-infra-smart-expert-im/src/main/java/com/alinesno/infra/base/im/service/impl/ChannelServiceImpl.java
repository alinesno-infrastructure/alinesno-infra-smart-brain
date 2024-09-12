package com.alinesno.infra.base.im.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.im.mapper.ChannelMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.enums.HasDeleteEnums;
import com.alinesno.infra.smart.im.constants.ImConstants;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.enums.ChannelType;
import com.alinesno.infra.smart.im.service.IChannelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChannelServiceImpl extends IBaseServiceImpl<ChannelEntity, ChannelMapper> implements IChannelService {

    @Override
    public String createChannel(ChannelEntity entity) {

        entity.setChannelId(IdUtil.nanoId());
        entity.setChannelType(entity.getChannelType()) ;

        this.save(entity) ;

        return entity.getId()+"";
    }

    @Override
    public void removeChannel(Long channelId) {
        LambdaUpdateWrapper<ChannelEntity> updateWrapper = new LambdaUpdateWrapper<>() ;

        updateWrapper.eq(ChannelEntity::getId ,  channelId)
                .set(ChannelEntity::getHasDelete , HasDeleteEnums.ILLEGAL.value)
                .notIn(ChannelEntity::getChannelType , ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue());

        this.update(updateWrapper) ;
    }

    @Override
    public List<ChannelEntity> allMyChannel() {

        LambdaQueryWrapper<ChannelEntity> queryPersonPublicWrapper = new LambdaQueryWrapper<>() ;
        queryPersonPublicWrapper.eq(ChannelEntity::getHasDelete , HasDeleteEnums.LEGAL.value)
                .eq(ChannelEntity::getChannelType , ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue());

        List<ChannelEntity> personPublicChannel = list(queryPersonPublicWrapper) ;
        if(personPublicChannel.isEmpty()){
            ChannelEntity e = new ChannelEntity() ;

            e.setChannelName("个人公共频道");
            e.setChannelDesc("公共频道服务，用于公共交流");
            e.setChannelType(ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue());

            e.setIcon(ImConstants.DEFAULT_AVATAR) ;
            e.setChannelId(IdUtil.nanoId());

            this.save(e) ;
        }

        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.eq(ChannelEntity::getHasDelete , HasDeleteEnums.LEGAL.value) ;

        return list(queryWrapper);
    }

    @Override
    public List<ChannelEntity> allPublicChannel() {

        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.eq(ChannelEntity::getHasDelete , HasDeleteEnums.LEGAL.value) ;

        return list(queryWrapper) ;
    }

    @Override
    public void jobChannel(long userId, String channelId) {
        log.debug("userId = {} , channelId = {}" , userId , channelId);
    }

    @Override
    public Long getDefaultChannelId() {

        List<ChannelEntity> list = allMyChannel() ;
        long channelId = 0L;

        for(ChannelEntity e : list){
            if(e.getChannelType().equals(ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue())){
                channelId = e.getId() ;
            }
        }

        return channelId;
    }

    @Override
    public List<ChannelEntity> getRecommendChannel() {
        log.debug("获取推荐频道");

        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.eq(ChannelEntity::getChannelType, ChannelType.RECOMMEND_CHANNEL.getValue()) ;

        return list(queryWrapper);

    }

}
