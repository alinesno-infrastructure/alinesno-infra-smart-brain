package com.alinesno.infra.smart.brain.api;

import com.alinesno.infra.smart.brain.api.validator.ValidPromptId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * BrainTaskDto类
 * 用于表示Brain任务的数据传输对象
 */
@Data
@ValidPromptId
public class BrainTaskDto {

    @NotNull(message = "businessId cannot be null")
    private String businessId; // 业务ID

    private String promptId; // GPT角色ID
    private String systemContent; // GPT角色设定
    private String userContent; // GPT用户信息
}
