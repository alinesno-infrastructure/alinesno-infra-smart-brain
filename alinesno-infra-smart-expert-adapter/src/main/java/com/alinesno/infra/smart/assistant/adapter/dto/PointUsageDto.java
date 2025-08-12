package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * PointUsageDto 类用于表示积分使用情况。
 */
@Data
public class PointUsageDto implements Serializable {

    private String type ; // 类型(agent|scene)
    private Long accountId ; // 账户ID
    private Long orgId ; // 组织ID

    @Data
   static class AgentUsage {
       private String agentId ; // 代理ID
       private String agentName ; // 代理名称
       private String agentLevel ; // 代理等级
    }

    @Data
    static class SceneUsage {
       private String sceneId ; // 场景ID
       private String sceneName ; // 场景名称
       private String sceneLevel ; // 场景等级
    }

}
