package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.assistant.api.validate.ConditionalValidJson;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ConditionalValidJson
@Data
public class ToolRequestDto extends BaseDto {

    private String script;

    @NotBlank(message = "工具ID不能为空")
    private String toolId;

    @NotBlank(message = "工具类型不能为空")
    private String type;

    @NotBlank(message = "工具参数不能为空")
    private String params ;

}