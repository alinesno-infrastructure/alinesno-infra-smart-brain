package com.alinesno.infra.smart.assistant.publish.utils;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件消息业务分发器，负责根据不同类型的微信事件消息进行相应的处理和分发。
 * 该类接收一个包含微信事件消息信息的 Map，根据事件类型进行分类处理。
 */
@Slf4j
public class EventDispatcher {

    /**
     * 处理微信事件消息的核心方法。
     * 从传入的事件消息 Map 中提取事件类型信息，
     * 并根据不同的事件类型进行相应的日志记录。
     *
     * @param map 包含微信事件消息信息的 Map，键为事件消息字段名，值为对应字段的值。
     */
    public static void processEvent(Map<String, String> map) {
        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
            // 记录关注事件日志
            log.info("接收到关注事件");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
            // 记录取消关注事件日志
            log.info("接收到取消关注事件");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_SCAN)) {
            // 记录扫描二维码事件日志
            log.info("接收到扫描二维码事件");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_LOCATION)) {
            // 记录位置上报事件日志
            log.info("接收到位置上报事件");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_CLICK)) {
            // 记录自定义菜单点击事件日志
            log.info("接收到自定义菜单点击事件");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_VIEW)) {
            // 记录自定义菜单View事件日志
            log.info("接收到自定义菜单View事件");
        }
    }
}