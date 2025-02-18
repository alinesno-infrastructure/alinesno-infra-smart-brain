package com.alinesno.infra.smart.assistant.adapter.dto;


import lombok.Data;

/**
 * 使用情况实体类
 * 记录了操作过程中消耗的总token数
 */
@Data
public class Usage {
    // 总token数，用于计量操作的资源消耗
    private int total_tokens;
}
