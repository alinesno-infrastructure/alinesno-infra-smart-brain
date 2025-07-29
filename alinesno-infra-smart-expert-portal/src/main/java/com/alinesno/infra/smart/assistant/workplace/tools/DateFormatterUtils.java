package com.alinesno.infra.smart.assistant.workplace.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatterUtils {
    
    private static final long ONE_MINUTE = 60 * 1000;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final long ONE_WEEK = 7 * ONE_DAY;
    private static final long ONE_MONTH = 30 * ONE_DAY;
    private static final long ONE_YEAR = 365 * ONE_DAY;
    
    public static String getRelativeTime(Date date) {
        if (date == null) {
            return "";
        }
        
        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        
        if (diff < 0) {
            // 如果传入的日期在未来，直接返回格式化日期
            return formatDate(date);
        }
        
        if (diff < ONE_MINUTE) {
            return "刚刚";
        } else if (diff < 60 * ONE_MINUTE) {
            long minutes = diff / ONE_MINUTE;
            return minutes + "分钟前";
        } else if (diff < 12 * ONE_HOUR) {
            long hours = diff / ONE_HOUR;
            return hours + "小时前";
        } else if (diff < ONE_DAY) {
            return "半天前";
        } else if (diff < ONE_WEEK) {
            long days = diff / ONE_DAY;
            return days + "天前";
        } else if (diff < ONE_MONTH) {
            long weeks = diff / ONE_WEEK;
            return weeks + "周前";
        } else if (diff < ONE_YEAR) {
            long months = diff / ONE_MONTH;
            return months + "个月前";
        } else if (diff < 2 * ONE_YEAR) {
            return "一年前";
        } else if (diff < 3 * ONE_YEAR) {
            return "两年前";
        } else {
            return formatDate(date);
        }
    }
    
    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
}