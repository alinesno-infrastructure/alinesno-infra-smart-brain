package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 生成审查清单
 */
@Data
public class DocReviewGenReviewDto {

    @Min(value = 1, message = "场景ID不能为空")
    private long sceneId ;

    @NotBlank(message = "合同类型不能为空")
    private String contractType;  // 合同类型

    @NotBlank(message = "审查立场不能为空")
    private String reviewPosition;  // 审查立场

    @NotBlank(message = "审核列表选项不能为空")
    private String reviewListOption ;  // 审核列表选项

    private String reviewListKnowledgeBase;  // 审核列表知识库

}
