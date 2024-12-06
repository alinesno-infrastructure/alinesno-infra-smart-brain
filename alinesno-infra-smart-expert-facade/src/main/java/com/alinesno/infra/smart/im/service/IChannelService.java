package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.im.entity.ChannelEntity;

import java.util.List;

public interface IChannelService extends IBaseService<ChannelEntity> {

    /**
     * 初始化个人频道，每个人有且仅有一个人频道
     * @param accountId
     */
    void initPersonChannel(long accountId);

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
    List<ChannelEntity> allMyChannel(PermissionQuery query);

    /**
     * 查询出所有公共频道
     * @return
     */
    List<ChannelEntity> allPublicChannel(PermissionQuery query);

    /**
     * 用户加入频道
     * @param roleId
     * @param channelId
     */
    void jobChannel(long roleId, long channelId);

    /**
     * 获取到默认的渠道
     * @return
     */
    Long getDefaultChannelId(long query);

    /**
     * 获取到推荐频道
     * @return
     */
    List<ChannelEntity> getRecommendChannel();

    /**
     * 用户加入频道
     * @param accountId
     * @param channelId
     */
    void accountJoinChannel(long accountId, long channelId);

    /**
     * 批量生成频道
     * @param recommendChannels
     */
    void batchCreateChannel(List<ChannelEntity> recommendChannels);

    /**
     * 初始化组织频道
     * @param orgId
     */
    void initOrgChannel(long orgId);
}
