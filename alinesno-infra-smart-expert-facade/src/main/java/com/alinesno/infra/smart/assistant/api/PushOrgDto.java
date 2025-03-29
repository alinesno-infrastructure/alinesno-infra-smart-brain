package com.alinesno.infra.smart.assistant.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PushOrgDto {

    @Min(value = 1, message = "角色ID不能为空")
    private long roleId;

    @Min(value = 1, message = "组织ID不能为空")
    private long orgId;

    @NotBlank(message = "推送方式不能为空")
    private String pushType ;
}
