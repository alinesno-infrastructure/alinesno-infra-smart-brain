package com.alinesno.infra.smart.assistant.publish.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Data;

/**
 * 该类用于构建回复给微信服务器的消息对象。
 * 当服务器接收到微信用户的消息后，需要按照微信的 XML 格式规范
 * 构建回复消息，本类通过 XStream 注解将对象映射为符合规范的 XML 结构。
 */
@Data
@XStreamAlias("xml")
public class WechatReplyMessageDto {
    /**
     * 接收方账号（收到的 OpenID），即微信用户的 OpenID
     */
    @XStreamAlias("ToUserName")
    @XStreamAsAttribute
    private String toUserName;

    /**
     * 发送方账号（一个 OpenID），即公众号的 OpenID
     */
    @XStreamAlias("FromUserName")
    @XStreamAsAttribute
    private String fromUserName;

    /**
     * 消息创建时间，单位为秒
     */
    @XStreamAlias("CreateTime")
    @XStreamAsAttribute
    private long createTime;

    /**
     * 消息类型，固定为 text 表示文本消息
     */
    @XStreamAlias("MsgType")
    @XStreamAsAttribute
    private String msgType = "text";

    /**
     * 回复的文本消息内容
     */
    @XStreamAlias("Content")
    @XStreamAsAttribute
    private String content;
}