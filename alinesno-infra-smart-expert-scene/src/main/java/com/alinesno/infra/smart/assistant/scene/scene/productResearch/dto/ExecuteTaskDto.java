package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建任务请求参数
 */
@Data
public class ExecuteTaskDto {

    @NotNull(message = "任务ID不能为空")
    private Long taskId ;

    @NotNull(message = "场景ID不能为空")
    private Long sceneId ;

    @NotNull(message = "频道ID不能为空")
    private String channelStreamId ;

    private String type ;  // 任务类型
}
