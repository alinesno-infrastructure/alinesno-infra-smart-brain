package com.alinesno.infra.smart.brain.interp.utils;

public class UserInfoUtils {
    /**
     * 获取用户信息字符串。
     *
     * @return 用户信息字符串
     */
    public static String getUserInfoString() {
        String username = System.getProperty("user.name");
        String currentWorkingDirectory = System.getProperty("user.dir");
        String operatingSystem = System.getProperty("os.name");
        String defaultShell = System.getenv("SHELL");

        return String.format("[用户信息]\n用户名: %s\n当前工作目录: %s\n默认Shell: %s\n操作系统: %s",
                username, currentWorkingDirectory, defaultShell, operatingSystem);
    }
}