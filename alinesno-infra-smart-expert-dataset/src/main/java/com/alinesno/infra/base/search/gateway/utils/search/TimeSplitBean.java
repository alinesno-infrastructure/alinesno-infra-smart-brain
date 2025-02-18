package com.alinesno.infra.base.search.gateway.utils.search;

import lombok.Data;

@Data
public class TimeSplitBean {
    private String startTime;
    private String endTime;
    // 有一个值取这两个时间的中间时间
    private String middleTime;
}