package com.alinesno.infra.smart.assistant.api.adapter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * BrainTaskDto类
 * 用于表示Brain任务的数据传输对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BrainTaskDto {

    @NotNull(message = "businessId cannot be null")
    private String businessId; // 业务ID

    @NotNull(message = "promptId cannot be null")
    private String promptId; // GPT角色ID

    private Map<String , Object> params ; // params

}