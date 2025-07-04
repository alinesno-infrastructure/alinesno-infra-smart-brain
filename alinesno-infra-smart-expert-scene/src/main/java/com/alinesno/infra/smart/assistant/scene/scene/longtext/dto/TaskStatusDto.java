package com.alinesno.infra.smart.assistant.scene.scene.longtext.dto;

import lombok.Data;

/**
 * 描述：任务状态和进度信息
 * TaskStatusDto
 */
@Data
public class TaskStatusDto {

    private Long taskId;
    private String status;
    private int progress;

}