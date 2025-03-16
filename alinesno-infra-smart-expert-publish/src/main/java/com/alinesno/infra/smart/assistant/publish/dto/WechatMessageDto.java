package com.alinesno.infra.smart.assistant.publish.dto;

import lombok.Data;

/**
 * 该类用于封装从微信服务器接收到的消息。
 * 微信服务器在用户发送消息后，会将消息以 XML 格式发送到配置的服务器地址，
 * 本类将这些消息的关键信息进行封装，方便后续处理。
 */
@Data
public class WechatMessageDto {
    /**
     * 接收方账号（收到的 OpenID）
     */
    private String toUserName;
    /**
     * 发送方账号（一个 OpenID）
     */
    private String fromUserName;
    /**
     * 消息创建时间 （整型）
     */
    private String createTime;
    /**
     * 消息类型，如 text（文本消息）、image（图片消息）等
     */
    private String msgType;
    /**
     * 文本消息内容
     */
    private String content;
    /**
     * 消息 ID，64 位整型
     */
    private String msgId;
}