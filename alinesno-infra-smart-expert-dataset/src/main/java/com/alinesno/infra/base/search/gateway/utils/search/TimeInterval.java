package com.alinesno.infra.base.search.gateway.utils.search;

import lombok.Getter;

@Getter
public enum TimeInterval {
    FIFTEEN_MINUTES(15, "15分钟"),
    THIRTY_MINUTES(30, "30分钟"),
    ONE_HOUR(60, "1小时"),
    SIX_HOURS(360, "6小时"),
    TWELVE_HOURS(720, "12小时"),
    TWENTY_FOUR_HOURS(1440, "24小时"),
    THREE_DAYS(1440 * 3, "3天"),
    SEVEN_DAYS(1440 * 7, "7天"),
    ONE_MONTH(1440 * 30, "1个月"),
    ONE_YEAR(1440 * 365, "1年");

    private final int minutes;
    private final String label;

    TimeInterval(int minutes, String label) {
        this.minutes = minutes;
        this.label = label;
    }

}
