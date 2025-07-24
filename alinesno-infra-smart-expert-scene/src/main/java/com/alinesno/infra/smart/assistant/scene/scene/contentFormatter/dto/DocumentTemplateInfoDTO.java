package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentTemplateInfoDTO extends BaseDto {
    /**
     * 模板名称
     */
    @NotNull(message = "模板名称不能为空")
    private String name;

    /**
     * 模板描述
     */
    @NotNull(message = "模板描述不能为空")
    private String description;

    /**
     * 模板分组ID
     */
    @NotNull(message = "模板分组ID不能为空")
    private Long groupId;
}
