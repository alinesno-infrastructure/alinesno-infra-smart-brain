package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PPT 配置信息数据传输对象 (DTO)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleConfigDto {

    /**
     * 作业类型
     */
    @NotBlank(message = "作业类型不能为空")
    private String writingType;

    /**
     * PPT 目标受众（例如："investor" 表示投资者）
     * 不可为空
     */
    @NotBlank(message = "目标受众不能为空")
    private String audience;

    /**
     * 文章使用场景（例如："analysis" 表示分析报告）
     * 不可为空
     */
    @NotBlank(message = "使用场景不能为空")
    private String scenario;

    /**
     * PPT 风格基调（例如："professional" 表示专业风格）
     * 不可为空
     */
    @NotBlank(message = "风格基调不能为空")
    private String tone;
}