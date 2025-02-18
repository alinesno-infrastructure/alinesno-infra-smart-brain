package com.alinesno.infra.base.search.memory.constants;


import com.alinesno.infra.base.search.memory.enums.LanguageEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 假设 LanguageEnum 已经被定义好了
// import path.to.LanguageEnum;

public class DateTimeExpressions {

    // DATATIME_WORD_LIST
    private static final Map<LanguageEnum, List<String>> DATETIME_WORD_LIST = new HashMap<>() {{
        put(LanguageEnum.CN, Arrays.asList(
            "天", "周", "月", "年", "星期", "点", "分钟", "小时", "秒", "上午", "下午", "早上", "早晨", "晚上",
            "中午", "日", "夜", "清晨", "傍晚", "凌晨", "岁"
        ));
        put(LanguageEnum.EN, Arrays.asList(
            "year", "yr", "month", "mo", "week", "wk", "day", "d", "hour", "hr", "minute", "min", "second", "sec",
            "Monday", "Mon", "Tuesday", "Tue", "Tues", "Wednesday", "Wed", "Thursday", "Thu", "Thur", "Thurs",
            "Friday", "Fri", "Saturday", "Sat", "Sunday", "Sun",
            "January", "Jan", "February", "Feb", "March", "Mar", "April", "Apr", "May", "June", "Jun", "July", "Jul",
            "August", "Aug", "September", "Sep", "Sept", "October", "Oct", "November", "Nov", "December", "Dec",
            "Today", "Tomorrow", "Tmrw", "Yesterday", "Yday", "Now", "Morning", "AM", "a.m.", "Afternoon", "PM", "p.m.",
            "Evening", "Night", "Midnight", "Noon", "Spring", "Summer", "Autumn", "Fall", "Winter", "Century", "cent.",
            "Decade", "Millennium", "Quarter", "Q1", "Q2", "Q3", "Q4", "Semester", "Fortnight", "Weekend"
        ));
    }};

    // WEEKDAYS
    private static final Map<LanguageEnum, List<String>> WEEKDAYS = new HashMap<>() {{
        put(LanguageEnum.CN, Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        put(LanguageEnum.EN, Arrays.asList(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        ));
    }};

    // MONTH_DICT
    private static final Map<LanguageEnum, List<String>> MONTH_DICT = new HashMap<>() {{
        put(LanguageEnum.CN, Arrays.asList(
            "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"
        ));
        put(LanguageEnum.EN, Arrays.asList(
            "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"
        ));
    }};

    // NONE_WORD
    private static final Map<LanguageEnum, String> NONE_WORD = Map.of(
        LanguageEnum.CN, "无",
        LanguageEnum.EN, "none"
    );

    // REPEATED_WORD
    private static final Map<LanguageEnum, String> REPEATED_WORD = Map.of(
        LanguageEnum.CN, "重复",
        LanguageEnum.EN, "repeated"
    );

    // CONTRADICTORY_WORD
    private static final Map<LanguageEnum, String> CONTRADICTORY_WORD = Map.of(
        LanguageEnum.CN, "矛盾",
        LanguageEnum.EN, "contradiction"
    );

    // CONTAINED_WORD
    private static final Map<LanguageEnum, String> CONTAINED_WORD = Map.of(
        LanguageEnum.CN, "被包含",
        LanguageEnum.EN, "contained"
    );

    // COLON_WORD
    private static final Map<LanguageEnum, String> COLON_WORD = Map.of(
        LanguageEnum.CN, "：",
        LanguageEnum.EN, ":"
    );

    // COMMA_WORD
    private static final Map<LanguageEnum, String> COMMA_WORD = Map.of(
        LanguageEnum.CN, "，",
        LanguageEnum.EN, ","
    );

    // DEFAULT_HUMAN_NAME
    private static final Map<LanguageEnum, String> DEFAULT_HUMAN_NAME = Map.of(
        LanguageEnum.CN, "用户",
        LanguageEnum.EN, "user"
    );

    // DATATIME_KEY_MAP
    private static final Map<LanguageEnum, Map<String, String>> DATETIME_KEY_MAP = new HashMap<>() {{
        put(LanguageEnum.CN, Map.of(
            "年", "year",
            "月", "month",
            "日", "day",
            "周", "week",
            "星期几", "weekday"
        ));
        put(LanguageEnum.EN, Map.of(
            "Year", "year",
            "Month", "month",
            "Day", "day",
            "Week", "week",
            "Weekday", "weekday"
        ));
    }};

    // TIME_INFER_WORD
    private static final Map<LanguageEnum, String> TIME_INFER_WORD = Map.of(
        LanguageEnum.CN, "推断时间",
        LanguageEnum.EN, "Inference time"
    );

    // USER_NAME_EXPRESSION
    private static final Map<LanguageEnum, String> USER_NAME_EXPRESSION = Map.of(
        LanguageEnum.CN, "用户姓名是{name}。",
        LanguageEnum.EN, "User's name is {name}."
    );

    // Getters for the constants (if needed)
    public static Map<LanguageEnum, List<String>> getDateTimeWordList() {
        return DATETIME_WORD_LIST;
    }

    public static Map<LanguageEnum, List<String>> getWeekdays() {
        return WEEKDAYS;
    }

    public static Map<LanguageEnum, List<String>> getMonthDict() {
        return MONTH_DICT;
    }

    public static Map<LanguageEnum, String> getNoneWord() {
        return NONE_WORD;
    }

    public static Map<LanguageEnum, String> getRepeatedWord() {
        return REPEATED_WORD;
    }

    public static Map<LanguageEnum, String> getContradictoryWord() {
        return CONTRADICTORY_WORD;
    }

    public static Map<LanguageEnum, String> getContainedWord() {
        return CONTAINED_WORD;
    }

    public static Map<LanguageEnum, String> getColonWord() {
        return COLON_WORD;
    }

    public static Map<LanguageEnum, String> getCommaWord() {
        return COMMA_WORD;
    }

    public static Map<LanguageEnum, String> getDefaultHumanName() {
        return DEFAULT_HUMAN_NAME;
    }

    public static Map<LanguageEnum, Map<String, String>> getDateTimeKeyMap() {
        return DATETIME_KEY_MAP;
    }

    public static Map<LanguageEnum, String> getTimeInferWord() {
        return TIME_INFER_WORD;
    }

    public static Map<LanguageEnum, String> getUserNameExpression() {
        return USER_NAME_EXPRESSION;
    }
}