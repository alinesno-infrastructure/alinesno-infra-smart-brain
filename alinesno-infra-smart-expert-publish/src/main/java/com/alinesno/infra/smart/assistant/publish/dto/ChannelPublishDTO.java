package com.alinesno.infra.smart.assistant.publish.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 发布渠道数据传输对象，用于在不同层之间传输发布渠道信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelPublishDTO extends BaseDto {

    /**
     * 角色ID，标识与该发布渠道相关联的角色
     */
    private long roleId;

    /**
     * 发布渠道的名称
     */
    private String name;

    /**
     * 发布渠道的描述信息
     */
    private String description;

    /**
     * 图标类名，用于表示发布渠道的图标
     */
    private String iconClass;

    /**
     * 参数键，用于唯一标识发布渠道的配置参数
     */
    private String paramKey;

    /**
     * 是否已配置的标志，1表示已配置，0表示未配置
     */
    private int hasConfigured;

    /**
     * 配置映射，存储发布渠道的相关配置信息
     */
    private Map<String , Object> configMap ;

}
