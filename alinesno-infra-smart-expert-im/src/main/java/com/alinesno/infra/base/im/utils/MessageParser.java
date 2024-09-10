package com.alinesno.infra.base.im.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

    /**
     * 解析消息中的用户名和项目代号。
     */
    public static ParsedData parseMessage(String message) {
        List<String> usernames = new ArrayList<>();
        List<String> projectCodes = new ArrayList<>();

        // 匹配 @ 用户名
        Pattern atPattern = Pattern.compile("@(\\w+)");
        Matcher atMatcher = atPattern.matcher(message);
        while (atMatcher.find()) {
            usernames.add(atMatcher.group(1));
        }

        // 匹配 # 项目代号
        Pattern hashPattern = Pattern.compile("#(\\d+\\.\\d+)");
        Matcher hashMatcher = hashPattern.matcher(message);
        while (hashMatcher.find()) {
            projectCodes.add(hashMatcher.group(1));
        }

        return new ParsedData(usernames, projectCodes);
    }

    @Data
    public static class ParsedData {
        private final List<String> usernames;
        private final List<String> businessIds;

        public ParsedData(List<String> usernames, List<String> businessIds) {
            this.usernames = usernames;
            this.businessIds = businessIds;
        }
    }
}