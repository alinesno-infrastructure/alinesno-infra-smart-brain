package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.smart.assistant.api.config.RoleReActConfigDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色运行脚本
 * 该类用于表示一个角色在特定场景下运行的脚本信息，包括脚本类型和脚本内容
 */
@ToString
@Data
public class RoleScriptDto implements Serializable {

    @Min(value = 1, message = "角色ID不能为空")
    private long roleId; // 角色ID，用于标识脚本所属的角色

    private RoleReActConfigDto agentConfigParams;

    @NotBlank(message = "脚本内容不能为空")
    private String script; // 脚本内容，具体执行、审核或函数调用的脚本

    @NotBlank(message = "脚本类型不能为空")
    private String type; // 类型，标识脚本的用途，可以是execute（执行）、audit（审核）、functionCall（函数调用）

    @NotBlank(message = "验证用户请求文本不能为空")
    private String message ; // 验证用户请求文本
}
