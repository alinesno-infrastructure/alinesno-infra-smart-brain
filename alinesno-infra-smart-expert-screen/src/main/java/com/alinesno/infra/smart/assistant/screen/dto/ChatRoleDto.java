package com.alinesno.infra.smart.assistant.screen.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 与角色交互的数据传输对象
 */
@Data
public class ChatRoleDto implements Serializable {

    @Min(value = 1, message = "screenId不能为空")
    private long screenId ;  // 场景ID

    @NotBlank(message = "message不能为空")
    private String message ; // 消息内容

    @Min(value = 1, message = "channelId不能为空")
    private long roleId ; // 角色ID

}
