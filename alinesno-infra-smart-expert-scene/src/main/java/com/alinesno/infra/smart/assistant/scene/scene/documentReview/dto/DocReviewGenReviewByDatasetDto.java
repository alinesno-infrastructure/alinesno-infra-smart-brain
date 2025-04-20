package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 生成审查清单
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocReviewGenReviewByDatasetDto extends DocReviewGenReviewDto {

    @NotNull(message = "审查清单ID不能为空")
    private Long auditId ;

}
