package com.alinesno.infra.base.im.service;

import com.alinesno.infra.base.im.entity.ChannelUserEntity;
import com.alinesno.infra.base.im.entity.UserEntity;
import com.alinesno.infra.common.facade.services.IBaseService;

import java.util.List;

public interface IChannelUserService extends IBaseService<ChannelUserEntity> {

    /**
     * 获取到频道的Agent列表
     *
     * @param channelId
     * @return
     */
    List<UserEntity> getChannelAgent(String channelId);

}
