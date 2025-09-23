package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
 * 用户积分DTO，账户id/已用积分/剩余积分/用户等级
 */
@Data
public class OrgPointDto {

    private int usedPoint;  // 已用积分
    private Integer points;   // 用户当前积分（注意字段名是points不是pointValue）
    private String userLevel;  // 用户等级
    private String packageName ; // 套餐名称
    private Long orgId ; // 组织ID

    private int agentMaxConcurrency;  // Agent最大并发数
    private int sceneMaxConcurrency;  // 场景最大并发数

    private String state ; // 状态(SUCCESS/FAIL)
    private String failMessage ;  // 失败信息

    /**
     * 获取一个空的用户积分DTO
      * @return
     */
    public static OrgPointDto empty() {
        OrgPointDto dto = new OrgPointDto();

        dto.setPoints(0);
        dto.setUsedPoint(0);

        dto.setAgentMaxConcurrency(1) ;
        dto.setSceneMaxConcurrency(1) ;

        return dto ;
    }
}
