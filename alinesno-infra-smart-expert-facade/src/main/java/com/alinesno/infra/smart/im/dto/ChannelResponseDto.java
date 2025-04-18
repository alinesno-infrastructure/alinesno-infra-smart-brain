package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 频道实体类
 * 用于表示即时通讯中的频道相关信息，继承自BaseDto以获取基础的DTO功能
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("channel")
public class ChannelResponseDto extends BaseDto {

    /**
     * 频道描述
     * 用于简要描述频道的主题或用途
     */
    private String channelDesc;

    /**
     * 频道图标
     * 用于表示频道的图标地址或名称
     */
    private String icon;

    /**
     * 频道ID
     * 唯一标识一个频道
     */
    private String channelId;

    /**
     * 频道名称
     * 频道的名称，用于显示在界面上
     */
    private String channelName;

    /**
     * 频道类型
     * 用于区分不同类型的频道，例如公开频道、私有频道等
     */
    private String channelType;

    /**
     * 频道进入密钥
     * 用户进入频道所需的密钥，用于权限控制
     */
    private String channelKey;

    /**
     * 知识库ID
     * 关联的知识库的ID，用于存储与频道相关的知识
     */
    private String knowledgeId;

    /**
     * 知识库类型
     * 以|号进行分割的知识库类型，用于进一步细化知识库的分类
     */
    private String knowledgeType;

    /**
     * 组织名称
     * 频道所属的组织名称，用于显示组织信息
     */
    private String orgName;

    /**
     * 智能体数量
     * 表示在该频道中的智能体的数量
     */
    private int agentCount;

    /**
     * 聊天次数
     * 表示在该频道中发生的聊天次数，用于统计分析
     */
    private int chatCount;
}
