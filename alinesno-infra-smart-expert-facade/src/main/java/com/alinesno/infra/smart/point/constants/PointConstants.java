package com.alinesno.infra.smart.point.constants;

/**
 * 积分常量类
 */
public interface PointConstants {

    // 组织级计数器键模式
    String POINT_KEY_PREFIX = "aip_point_org";

    // 纯组织级计数器键模式： org:counter:{orgId}
    String ORG_COUNTER_KEY = "aip_point_org:counter:%d";

    // 新增场景任务Key
    String ORG_TASK_COUNTER_KEY = "aip_point_org:task:counter:%d";

}
