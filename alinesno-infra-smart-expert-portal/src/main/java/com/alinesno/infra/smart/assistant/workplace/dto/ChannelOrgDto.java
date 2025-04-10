package com.alinesno.infra.smart.assistant.workplace.dto;

import com.alinesno.infra.smart.im.entity.ChannelEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ChannelOrgDto类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelOrgDto extends ChannelEntity {
    private String orgName;
}
