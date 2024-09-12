package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.entity.ChannelRoleEntity;

import java.util.List;

public interface IChannelRoleService extends IBaseService<ChannelRoleEntity> {

    /**
     * 获取到频道的Agent列表
     *
     * @param channelId
     * @return
     */
    List<IndustryRoleEntity> getChannelAgent(String channelId);

}
