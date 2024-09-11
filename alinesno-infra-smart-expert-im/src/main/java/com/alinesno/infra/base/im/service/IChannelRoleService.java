package com.alinesno.infra.base.im.service;

import com.alinesno.infra.base.im.entity.ChannelRoleEntity;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;

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
