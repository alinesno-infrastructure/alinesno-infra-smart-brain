package com.alinesno.infra.base.im.service;

import com.alinesno.infra.base.im.entity.ChannelEntity;
import com.alinesno.infra.common.facade.services.IBaseService;

import java.util.List;

public interface IChannelService extends IBaseService<ChannelEntity> {

    /**
     * 保存和创建Channel
     *
     * @param entity
     * @return 最新保存的ID
     */
    String createChannel(ChannelEntity entity);

    /**
     * 逻辑删除频道
     * @param channelId
     */
    void removeChannel(Long channelId);

    /**
     * 查询用户所有的渠道
     * @return
     */
    List<ChannelEntity> allMyChannel();

    /**
     * 查询出所有公共频道
     * @return
     */
    List<ChannelEntity> allPublicChannel();

    /**
     * 用户加入频道
     * @param userId
     * @param channelId
     */
    void jobChannel(long userId, String channelId);

    /**
     * 获取到默认的渠道
     * @return
     */
    Long getDefaultChannelId();
}
