package com.alinesno.infra.smart.brain.api;

import lombok.Data;

@Data
public class BrainTaskDto {

    private String businessId ; // 业务ID

    private String promptId ; // GPT角色ID
    private String systemContent ;  // GPT角色设定
    private String userContent ;  // GPT用户信息
}
