package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReviewContentRequestDTO extends ContentFormatterRequestDTO {

    @NotEmpty(message = "校验规则")
    private List<@NotNull Long> ruleIds;

}    