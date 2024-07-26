package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.ChannelEntity;
import com.alinesno.infra.smart.assistant.mapper.ChannelMapper;
import com.alinesno.infra.smart.assistant.service.IChannelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class ChannelServiceImpl extends IBaseServiceImpl<ChannelEntity, ChannelMapper> implements IChannelService {

    @Override
    public String getRoleIdByRobotKey(String robotKey) {

        Assert.hasLength(robotKey , "RobotKey为空");

        LambdaQueryWrapper<ChannelEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(ChannelEntity::getRobotKey , robotKey) ;

        return this.getOne(wrapper).getRoleId() + "";
    }
}