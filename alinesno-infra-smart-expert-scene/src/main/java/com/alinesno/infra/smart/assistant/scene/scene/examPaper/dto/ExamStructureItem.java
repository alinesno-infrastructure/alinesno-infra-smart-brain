package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

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

    @NotNull(message = "题目数量不能为空")
    private Integer total_questions;

    @NotNull(message = "每题分数不能为空")
    private Integer score_per_question;
}