package com.alinesno.infra.smart.assistant.api.wechat;

/**
 * 链接消息类
 * 该类继承自BaseMessage，表示微信中的链接消息
 */
public class LinkMessage extends BaseMessage{
    private String Title; // 链接消息的标题
    private String Description; // 链接消息的描述
    private String Url; // 链接消息的链接地址

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
