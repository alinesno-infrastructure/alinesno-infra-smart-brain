package com.alinesno.infra.base.im.utils;

import cn.hutool.json.JSONUtil;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.ChatSendMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;

public class TaskUtils {

    /**
     * 生成任务信息
     * @param message
     * @param role
     * @return
     */
    public static MessageTaskInfo genderTaskInfo(ChatSendMessageDto message, IndustryRoleEntity role) {

        String chatText = message.getMessage() ;

        MessageParser.ParsedData parsedData = MessageParser.parseMessage(message.getMessage());

        MessageTaskInfo msg = new MessageTaskInfo();

        msg.setChannelId(message.getChannelId());
        msg.setBusinessId(String.valueOf(0));
        msg.setRoleId(role.getId());
        msg.setText(chatText);
        msg.setPreBusinessId(JSONUtil.toJsonStr(parsedData.getBusinessIds())) ;
        msg.setRoleDto(role);

        // 账户信息
        msg.setAccountId(message.getAccountId());
        msg.setAccountName(message.getAccountName());
        msg.setAccountIcon(message.getAccountIcon());

        return msg ;

    }

    /**
     * 格式化两个时间戳之间的时间差，返回类似 "x时x分x秒xxx" 的字符串。
     * 如果某单位（小时、分钟或秒）没有被使用到，则前面不会显示该单位。
     *
     * @param startTime 开始时间（毫秒）
     * @param endTime 结束时间（毫秒）
     * @return 时间差格式化的字符串
     */
    public static String formatTimeDifference(long startTime, long endTime) {
        // 计算两个时间戳之间的时间差（毫秒）
        long diff = endTime - startTime;

        // 分别计算小时、分钟、秒和毫秒
        long hours = diff / (1000 * 60 * 60);
        diff %= (1000 * 60 * 60); // 取模后得到剩余的时间
        long minutes = diff / (1000 * 60);
        diff %= (1000 * 60); // 再次取模后得到剩余的时间
        long seconds = diff / 1000;
        diff %= 1000; // 最后得到剩余的毫秒数

        // 构建时间差字符串
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("时");
        }
        if (minutes > 0 || !sb.isEmpty()) { // 如果有分钟或者前面已经有小时了
            sb.append(minutes).append("分");
        }
        if (seconds > 0 || !sb.isEmpty()) { // 如果有秒或者前面已经有分钟了
            sb.append(seconds).append("秒");
        }
        if (diff > 0 || !sb.isEmpty()) { // 如果有毫秒或者前面已经有秒了
            sb.append(diff).append("毫秒");
        }

        return sb.toString();
    }
}
