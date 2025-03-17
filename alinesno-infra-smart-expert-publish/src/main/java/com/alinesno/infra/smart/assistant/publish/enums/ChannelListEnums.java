package com.alinesno.infra.smart.assistant.publish.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道列表枚举类
 */
@Getter
public enum ChannelListEnums {

    WEB_VERSION("网页版", "可通过PC或移动设备立即开始对话", "fa-brands fa-edge", "web_version", false),
//    CUSTOMER_SERVICE("客服", "提供客户服务接入系统的渠道", "fas fa-user-tie", "customer_service", false),
//    WECHAT_OFFICIAL_ACCOUNT("微信公众号", "可推送消息到用户的订阅渠道", "fab fa-weixin", "wechat_official_account", false),
//    DINGTALK("钉钉", "与钉钉平台集成的渠道", "fab fa-alipay", "dingtalk", false),
//    API_INTERFACE("API接口", "提供系统间数据交互的接口渠道", "fas fa-plug", "api_interface", false),
    WEB_EMBEDDED("网站嵌入", "通过 iframe/JS 将应用以页面或聊天气泡的形式，快速集成到你的网站", "fas fa-comment-dots", "web_embedded", false);

    private final String name;
    private final String desc;
    private final String iconClass;
    private final String paramKey;
    private final boolean isConfigured;

    ChannelListEnums(String name, String desc, String iconClass, String paramKey, boolean isConfigured) {
        this.name = name;
        this.desc = desc;
        this.iconClass = iconClass;
        this.paramKey = paramKey;
        this.isConfigured = isConfigured;
    }

    /**
     * 通过paramKey获取到对象
     * @return
     */
    public static ChannelListEnums getByParamKey(String paramKey) {
        for (ChannelListEnums channel : values()) {
            if (channel.getParamKey().equals(paramKey)) {
                return channel;
            }
        }
        return null;
    }

    /**
     * 将枚举转换为列表
     * @return
     */
    public static List<Map<String, Object>> toListOfMaps() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChannelListEnums channel : values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", channel.getName());
            map.put("desc", channel.getDesc());
            map.put("iconClass", channel.getIconClass());
            map.put("paramKey", channel.getParamKey());
            map.put("isConfigured", channel.isConfigured());
            result.add(map);
        }
        return result;
    }
}