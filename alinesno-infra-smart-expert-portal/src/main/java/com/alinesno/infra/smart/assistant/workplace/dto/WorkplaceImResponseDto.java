package com.alinesno.infra.smart.assistant.workplace.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.im.dto.IndustryRoleOrgDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 工作台响应DTO类
 * 继承自BaseDto，用于工作台信息的传输和展示
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkplaceImResponseDto extends BaseDto {

    /**
     * 工作台名称
     */
    private String name;

    /**
     * 工作台描述
     */
    private String description;

    /**
     * 工作台背景图片地址
     */
    private String backgroundImage;

    /**
     * 工作台类型
     */
    private String workplaceType;

    /**
     * 工作台来源
     */
    private String workplaceSource;

    /**
     * 来源工作台ID
     */
    private long sourceWorkplaceId;

    /**
     * 频道列表
     */
    private List<ChannelOrgDto> channelsList;

    /**
     * 智能助手列表
     */
    private List<IndustryRoleOrgDto> agentsList;

    /**
     * 场景列表
     */
    private List<SceneOrgDto> scenesList;
}
