package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * 配置DTO
 */
@ToString
@Data
public class ConfigDto {

    @NotNull(message = "模板不能为空")
    private Long templateId;

    @NotNull(message = "resultConfig不能为空")
    private ResultConfig resultConfig;

    @NotNull(message = "config不能为空")
    private MainConfig config;

    @Data
    public static class ResultConfig {
        @NotBlank(message = "enable不能为空")
        private String enable;

        @NotBlank(message = "config不能为空")
        private String config;
    }

    @Data
    public static class MainConfig {
        @NotBlank(message = "planTemplate不能为空")
        private String planTemplate;

        @NotBlank(message = "contentTemplate不能为空")
        private String contentTemplate;
    }
}