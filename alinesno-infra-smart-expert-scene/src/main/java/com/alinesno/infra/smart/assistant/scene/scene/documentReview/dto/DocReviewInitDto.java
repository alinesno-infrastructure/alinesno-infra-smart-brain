package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 文档审核初始化数据传输对象
 */
@Data
public class DocReviewInitDto {

    @Min(value = 1 , message = "场景ID不能为空")
    private long sceneId ;

    @Min(value = 1 , message = "分析智能工程师不能为空")
    private long analysisAgentEngineer; // 分析智能工程师

    @Min(value = 1 , message = "逻辑审核工程师不能为空")
    private long logicReviewerEngineer; // 逻辑审核工程师
}
