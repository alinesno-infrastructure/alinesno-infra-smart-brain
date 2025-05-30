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
     * PPT 页码范围（例如："5-10"）
     * 不可为空
     */
    @NotBlank(message = "页码范围不能为空")
    private String pages;

    /**
     * PPT 目标受众（例如："investor" 表示投资者）
     * 不可为空
     */
    @NotBlank(message = "目标受众不能为空")
    private String audience;

    /**
     * PPT 使用场景（例如："analysis" 表示分析报告）
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