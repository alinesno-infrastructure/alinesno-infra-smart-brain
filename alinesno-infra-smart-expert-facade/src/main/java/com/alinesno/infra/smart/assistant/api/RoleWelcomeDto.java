package com.alinesno.infra.smart.assistant.api;

import com.alibaba.fastjson.JSONObject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.checkerframework.checker.units.qual.N;

/**
 * 角色欢迎信息数据传输对象
 */
@Data
public class RoleWelcomeDto {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId ;

    /**
     * 角色配置的欢迎信息
     */
    @NotNull(message = "角色配置的欢迎信息不能为空")
    private JSONObject value ;

}
