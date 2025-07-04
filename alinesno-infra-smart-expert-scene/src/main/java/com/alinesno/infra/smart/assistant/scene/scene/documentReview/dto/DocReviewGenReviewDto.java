package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 生成审查清单
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocReviewGenReviewDto extends BaseDto {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId ;

    @NotNull(message = "任务ID不能为空")
    private Long taskId ;

    @NotNull(message = "渠道流ID不能为空")
    private Long channelStreamId ;

    @NotBlank(message = "合同类型不能为空")
    private String contractType;  // 合同类型

    @NotBlank(message = "审查立场不能为空")
    private String reviewPosition;  // 审查立场

    @NotBlank(message = "审核列表选项不能为空")
    private String reviewListOption ;  // 审核列表选项

    private String reviewListKnowledgeBase;  // 审核列表知识库

}
