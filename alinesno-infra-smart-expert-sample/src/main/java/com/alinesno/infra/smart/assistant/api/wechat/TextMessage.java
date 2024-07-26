package com.alinesno.infra.smart.assistant.api.wechat;

/**
 * 文本消息类
 * 该类继承自BaseMessage，表示微信中的文本消息
 */
public class TextMessage extends BaseMessage{
    private String Content; // 文本消息的内容

    /**
     * 获取文本消息的内容
     * @return 文本消息的内容
     */
    public String getContent() {
        return Content;
    }

    /**
     * 设置文本消息的内容
     * @param content 文本消息的内容
     */
    public void setContent(String content) {
        Content = content;
    }
}
