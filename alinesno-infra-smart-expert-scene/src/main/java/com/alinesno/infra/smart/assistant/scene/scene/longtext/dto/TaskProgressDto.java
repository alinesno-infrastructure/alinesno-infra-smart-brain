package com.alinesno.infra.smart.assistant.scene.scene.longtext.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 任务进度数据传输对象
 * 
 * 状态值:
 * PENDING - 等待中
 * PROCESSING - 处理中
 * COMPLETED - 已完成
 * FAILED - 已失败
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskProgressDto {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 进度百分比 (0-100)
     */
    private Integer progress;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 预计剩余时间 (秒)
     */
    private Long estimatedRemainingTime;
    
    /**
     * 当前处理阶段描述
     */
    private String currentStage;
    
    /**
     * 处理结果 (当status=COMPLETED时有效)
     */
    private Object result;
    
    /**
     * 错误信息 (当status=FAILED时有效)
     */
    private String errorMessage;
}