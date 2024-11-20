package com.alinesno.infra.smart.assistant.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReActRoleScriptDto {

    @Min(value = 1, message = "角色ID不能为空")
    private long roleId; // 角色ID，用于标识脚本所属的角色

    @NotBlank(message = "验证用户请求文本不能为空")
    private String message ; // 验证用户请求文本

}
