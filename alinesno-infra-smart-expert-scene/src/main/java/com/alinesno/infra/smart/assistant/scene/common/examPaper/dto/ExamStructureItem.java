package com.alinesno.infra.smart.assistant.scene.common.examPaper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 考试结构项DTO
 */
@Data
public class ExamStructureItem {

    @NotBlank(message = "ID不能为空")
    private String id;

    @NotBlank(message = "题型不能为空")
    private String type;

    @NotBlank(message = "题型名称不能为空")
    private String typeName ;

    @NotBlank(message = "题型描述不能为空")
    private String typeDesc ;

    @NotNull(message = "题目数量不能为空")
    private Integer totalQuestion;

    @NotNull(message = "每题分数不能为空")
    private Integer scorePerQuestion;
}