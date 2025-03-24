package com.alinesno.infra.base.im.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class MessageFormatter {

    public static String getSafeString(String input) {
        return input == null || "null".equals(input) ? "" : input ;
    }

    public static List<MsgMessageContent> formatMessage(String message) {
        String[] words = message.split(" ");
        List<MsgMessageContent> formattedMessage = new ArrayList<>();

        for (String word : words) {
            if (word.startsWith("@")) {
                formattedMessage.add(new MsgMessageContent("mention", word)) ;
            } else if (word.startsWith("#")) {
                String orderId = word.substring(1);
                formattedMessage.add(new MsgMessageContent("business", orderId));
            } else {
                formattedMessage.add(new MsgMessageContent("text", word));
            }
        }

        return formattedMessage;
    }

    /**
     * 消息处理工具类
     * @param message
     * @return
     */
    public static String getMessage(String message) {
        List<MsgMessageContent> formattedMessage = formatMessage(message);

        StringBuilder messageContent = new StringBuilder();
        for (MsgMessageContent content : formattedMessage) {
            if ("mention".equals(content.getType())) {
                messageContent.append("<span class=\"mention\">").append(content.getValue()).append("</span>");
            } else if ("business".equals(content.getType())) {
                messageContent.append("<span class=\"mention-business\">").append(content.getValue()).append("</span>");
            } else if ("text".equals(content.getType())){
                messageContent.append(content.getValue()).append(" ");
            }
        }
        return messageContent.toString();
    }

    @Data
    public static class MsgMessageContent {
        private String type;
        private String value;

        public MsgMessageContent(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    public static String clearMessage(String message) {
        String[] words = message.split(" ");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.startsWith("@") && !word.startsWith("#")) {
                result.append(word);
            }
        }

        return result.toString();
    }
}