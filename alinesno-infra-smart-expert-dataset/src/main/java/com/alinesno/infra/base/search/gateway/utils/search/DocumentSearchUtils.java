package com.alinesno.infra.base.search.gateway.utils.search;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * DocumentSearchUtils
 */
public class DocumentSearchUtils {

    /**
     * 实现日期的分割,并返回数组，分割按分钟为单位，可以自定义分钟,比如以2分钟为单位
     * 传输入开始时间和结束时间段，返回时间段数组
     */
    public List<TimeSplitBean> timeSplit(String startTime, String endTime, int minute) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);

        List<TimeSplitBean> timeSegments = new ArrayList<>();
        while (!startDateTime.isAfter(endDateTime)) {
            LocalDateTime nextSegmentStart = startDateTime.plusMinutes(minute);
            if (nextSegmentStart.isAfter(endDateTime)) {
                nextSegmentStart = endDateTime;
            }
            TimeSplitBean segment = new TimeSplitBean();
            segment.setStartTime(startDateTime.format(formatter));
            segment.setEndTime(nextSegmentStart.format(formatter));
            segment.setMiddleTime(startDateTime.plusMinutes(minute / 2).format(formatter));

            timeSegments.add(segment);
            startDateTime = nextSegmentStart.plusMinutes(1); // 移动到下一分钟，避免时间重叠
        }

        return timeSegments;
    }

    /**
     * 获取到传输时间一个小时内的分段，返回一个数组
     */
    public List<TimeSplitBean> getOneHourTimeSegments(String startTime, String endTime) {
        return timeSplit(startTime, endTime, 60);
    }

    /**
     * 自定义传输时间以
     * 15分钟/半个小时/1个小时/6个小时/12个小时/24个小时/3天/7天/1个月/1年的分段，并列成枚举形式
     * 分割返回数组，
     * 如果数组返回的数组超过100，则自动调整分割时间大小，使得返回尽量在100以内
     */
    public List<TimeSplitBean> getDefineTimeSegments(TimeInterval timeInterval, String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);

        List<TimeSplitBean> timeSegments = new ArrayList<>();
        int currentMinuteInterval = timeInterval.getMinutes();

        // 尝试分割时间，如果超过100个分段，则增加时间间隔
        while (true) {
            timeSegments.clear();
            timeSegments.addAll(timeSplit(startTime, endTime, currentMinuteInterval));

            // 如果分段数量不超过100，直接返回
            if (timeSegments.size() <= 100) {
                return timeSegments;
            }

            // 如果超过100个分段，尝试增加时间间隔
            currentMinuteInterval *= 2; // 每次增加一倍
        }
    }

    /**
     * 自定义传输时间以
     * 15分钟/半个小时/1个小时/6个小时/12个小时/24个小时/3天/7天/1个月/1年的分段，并列成枚举形式
     * 分割返回数组，
     * 如果数组返回的数组超过100，则自动调整分割时间大小，使得返回尽量在100以内
     */
    public List<TimeSplitBean> getDefineTimeSegments(TimeInterval timeInterval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDateTime = now.plusDays(1); // 结束时间为当前时间加一天，可以根据需要调整

        List<TimeSplitBean> timeSegments = new ArrayList<>();
        int currentMinuteInterval = 1 ; // timeInterval.getMinutes();

        // 尝试分割时间，如果超过100个分段，则增加时间间隔
        while (true) {
            timeSegments.clear();
            timeSegments.addAll(timeSplit(now, endDateTime, currentMinuteInterval));

            // 如果分段数量不超过100，直接返回
            if (timeSegments.size() <= 100) {
                return timeSegments;
            }

            // 如果超过100个分段，尝试增加时间间隔
            currentMinuteInterval *= 2; // 每次增加一倍
        }
    }

    public static String dateTimeNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    public static String addHours(String time, int hours) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        LocalDateTime endTime = localDateTime.plus(hours, ChronoUnit.HOURS);
        return endTime.format(formatter);
    }

    /**
     * 修改timeSplit方法使其接受LocalDateTime类型
      */
    private List<TimeSplitBean> timeSplit(LocalDateTime startDateTime, LocalDateTime endDateTime, int minute) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<TimeSplitBean> timeSegments = new ArrayList<>();
        while (!startDateTime.isAfter(endDateTime)) {
            LocalDateTime nextSegmentStart = startDateTime.plusMinutes(minute);
            if (nextSegmentStart.isAfter(endDateTime)) {
                nextSegmentStart = endDateTime;
            }
            TimeSplitBean segment = new TimeSplitBean();
            segment.setStartTime(startDateTime.format(formatter));
            segment.setEndTime(nextSegmentStart.format(formatter));
            segment.setMiddleTime(startDateTime.plusMinutes(minute / 2).format(formatter));

            timeSegments.add(segment);
            startDateTime = nextSegmentStart.plusMinutes(1); // 移动到下一分钟，避免时间重叠
        }

        return timeSegments;
    }

    /**
     * 只获取到日期，不需要年份，并确定异常的处理，比如 2023-07-01 00:31 只需要 00:31
     * @param middleTime
     * @return
     */
    private String getDatetime(String middleTime) {
        return middleTime.split(" ")[1];
    }
}