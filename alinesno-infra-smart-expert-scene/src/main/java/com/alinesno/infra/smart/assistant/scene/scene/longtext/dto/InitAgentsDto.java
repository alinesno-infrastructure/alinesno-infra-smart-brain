package com.alinesno.infra.smart.assistant.scene.scene.longtext.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 初始化Agent信息
 */
@Data
public class InitAgentsDto {

    @NotBlank(message = "场景ID不能为空")
    private String sceneId;  // 场景ID

    @NotBlank(message = "大纲设计工程师不能为空")
    private String outlineEngineer;

    @NotBlank(message = "章节工程师不能为空")
    private String chapterEngineer;

    @NotBlank(message = "内容生成输入不能为空")
    private String contentInput;



}