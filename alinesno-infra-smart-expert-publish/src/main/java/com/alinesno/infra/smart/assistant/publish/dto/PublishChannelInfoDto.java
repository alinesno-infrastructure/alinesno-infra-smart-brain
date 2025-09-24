package com.alinesno.infra.smart.assistant.publish.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * PublishChannelInfoDto类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PublishChannelInfoDto extends BaseDto {

    private String name;  // 渠道名称

    private String description;  // 渠道描述

    private String iconClass;  // 渠道图标类名

    private Long bannerLogo ;  // 渠道Banner图标

    /**
     * 渠道信息转换成DTO
     * @param channel
     * @return
     */
    public static PublishChannelInfoDto formEntity(ChannelPublishEntity channel) {

        PublishChannelInfoDto dto = new PublishChannelInfoDto();

        dto.setName(channel.getName());
        dto.setDescription(channel.getDescription());
        dto.setIconClass(channel.getIconClass());
        dto.setBannerLogo(channel.getBannerLogo());
        dto.setId(channel.getId());

        return dto;
    }
}
