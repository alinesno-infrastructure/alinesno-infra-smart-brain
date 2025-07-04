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

    AIP_AGENT_STORE("AIP商店", "在AIP商店里，你能高效地创建个性化智能助手，便捷地完成发布和共享操作。", "fa-solid fa-shop", "aip_agent_store", false),
    AIP_AGENT_SCENE("AIP场景", "借助AIP场景，你可针对不同场景快速定制智能助手，轻松发布并分享给他人。", "fa-solid fa-theater-masks", "aip_agent_scene", false),
    // AIP_AGENT_MARKET("AIP市场", "AIP市场为你提供了丰富资源，让你能创建出极具竞争力的智能助手并在市场发布共享。", "fa-solid fa-cart-shopping", "aip_agent_market", false),
    WEB_VERSION("网页版", "通过网页版，你能使用PC或移动设备随时随地开启与智能助手的对话。", "fa-brands fa-chrome", "web_version", false),
    // API_INTERFACE("API接口", "API接口作为系统间数据交互的桥梁，可实现智能助手数据的高效传输。", "fa-solid fa-code", "api_interface", false),
    WEB_EMBEDDED("网站嵌入", "使用网站嵌入功能，能通过iframe/JS迅速将智能助手集成到网站，提升用户体验。", "fa-solid fa-window-maximize", "web_embedded", false);

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