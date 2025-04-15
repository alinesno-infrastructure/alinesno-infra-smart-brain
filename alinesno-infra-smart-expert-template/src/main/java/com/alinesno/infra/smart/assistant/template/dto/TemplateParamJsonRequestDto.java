package com.alinesno.infra.smart.assistant.template.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模板参数JSON请求DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateParamJsonRequestDto extends BaseDto {

    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    @NotNull(message = "模板参数JSON不能为空")
    private String jsonStr;

}
