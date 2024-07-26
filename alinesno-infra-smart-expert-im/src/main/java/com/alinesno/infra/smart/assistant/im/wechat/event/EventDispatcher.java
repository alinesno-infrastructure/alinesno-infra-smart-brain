package com.alinesno.infra.smart.assistant.im.wechat.event;

import com.alinesno.infra.smart.assistant.im.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 事件消息业务分发器
 * 该类用于处理不同类型的事件消息，并进行相应的处理逻辑
 *
 * 作者：luoxiaodong
 * 版本：1.0.0
 */
public class EventDispatcher {

    private static final Logger log = LoggerFactory.getLogger(EventDispatcher.class);

    /**
     * 处理事件消息
     *
     * @param map 包含事件消息的键值对
     */
    public static void processEvent(Map<String, String> map) {

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) { //关注事件
            log.debug("这是关注事件！");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) { //取消关注事件
            log.debug("这是取消关注事件！");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_SCAN)) { //扫描二维码事件
            log.debug("这是扫描二维码事件！");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_LOCATION)) { //位置上报事件
            log.debug("这是位置上报事件！");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_CLICK)) { //自定义菜单点击事件
            log.debug("这是自定义菜单点击事件！");
        }

        if (map.get("Event").equals(MessageUtil.EVENT_TYPE_VIEW)) { //自定义菜单View事件
            log.debug("这是自定义菜单View事件！");
        }

    }
}
