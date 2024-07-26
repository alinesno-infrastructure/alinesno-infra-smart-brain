package com.alinesno.infra.smart.assistant.role.common;

public class RoleUtils {

    public static String formatTime(long startTime, long endTime) {
        long elapsedTime = endTime - startTime;

        long hours = elapsedTime / 3600000;
        long remainingMillis = elapsedTime % 3600000;
        long minutes = remainingMillis / 60000;
        remainingMillis %= 60000;
        long seconds = remainingMillis / 1000;
        long milliseconds = remainingMillis % 1000;

        StringBuilder formattedTime = new StringBuilder();

        if (hours > 0) {
            formattedTime.append(hours).append("时");
        }
        if (minutes > 0 || hours > 0) {
            formattedTime.append(minutes).append("分");
        }
        if (seconds > 0 || minutes > 0 || hours > 0) {
            formattedTime.append(seconds).append("秒");
        }
        formattedTime.append(milliseconds);

        return formattedTime.toString();
    }
}
