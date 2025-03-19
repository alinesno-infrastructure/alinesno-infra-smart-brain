package com.alinesno.infra.smart.assistant.role.utils;

import com.alinesno.infra.smart.im.constants.AgentConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.alinesno.infra.smart.im.constants.AgentConstants.ChatText.*;

/**
 * 去掉一些语句词句
 */
public class FilterWordUtils {

    private static final Set<String> ALL_TEXTS = new HashSet<>(Arrays.asList(
            CHAT_FINISH,
            CHAT_NO_ANSWER,
            CHAT_WAITING_WITH_TIME,
            CHAT_WAITING_NOT_TIME
    ));

    public static boolean filter(String chatText) {
        return !ALL_TEXTS.contains(chatText);
    }
}