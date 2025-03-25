package com.alinesno.infra.smart.utils;

import com.alinesno.infra.smart.im.constants.AgentConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.alinesno.infra.smart.im.constants.AgentConstants.ChatText.*;

/**
 * FilterWordUtils 类用于过滤特定的语句词句，避免这些词句在后续处理中出现。
 * 该类使用一个静态集合来存储需要过滤的文本，提供了判断文本是否需要过滤的方法。
 */
public class FilterWordUtils {

    /**
     * 静态常量集合，存储所有需要过滤的文本。
     * 这些文本来源于 AgentConstants.ChatText 类的静态常量。
     */
    private static final Set<String> ALL_TEXTS = new HashSet<>(Arrays.asList(
            // 聊天成功结束的标识文本
            CHAT_SUCCESS_FINISH,
            // 聊天失败结束的标识文本
            CHAT_FAIL_FINISH,
            // 通用的聊天结束标识文本
            CHAT_FINISH,
            // 带时间的等待提示标识文本
            CHAT_WAITING_WITH_TIME,
            // 不带时间的等待提示标识文本
            CHAT_WAITING_NOT_TIME
    ));

    /**
     * 过滤指定的聊天文本。
     * 该方法会调用 contains 方法判断文本是否包含在过滤集合中，
     * 如果不包含则返回 true，表示该文本可以通过过滤；
     * 如果包含则返回 false，表示该文本需要被过滤掉。
     *
     * @param chatText 需要过滤的聊天文本
     * @return 如果文本不包含在过滤集合中返回 true，否则返回 false
     */
    public static boolean filter(String chatText) {
        return !contains(chatText);
    }

    /**
     * 判断指定的聊天文本是否包含在过滤集合中。
     * 该方法会检查传入的文本是否存在于 ALL_TEXTS 集合中。
     *
     * @param chatText 需要检查的聊天文本
     * @return 如果文本包含在过滤集合中返回 true，否则返回 false
     */
    public static boolean contains(String chatText) {
        return ALL_TEXTS.contains(chatText);
    }
}