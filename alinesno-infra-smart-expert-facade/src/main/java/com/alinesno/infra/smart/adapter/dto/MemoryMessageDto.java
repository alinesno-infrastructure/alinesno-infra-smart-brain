package com.alinesno.infra.smart.adapter.dto;

import lombok.Data;

@Data
public class MemoryMessageDto {

    private String chatScopeId;
    private int sourceRoleId;
    private String sourceRoleName;
    private int targetRoleId;
    private String targetRoleName;
    private String data;
    private long timestamp;

}
