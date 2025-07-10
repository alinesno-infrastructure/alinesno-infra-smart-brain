package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

/**
 * 考试提交信息 DTO
 */
@Data
@ToString
public class ExamSubmissionDto {

    /**
     * 考试ID，不能为空
     */
    @NotBlank(message = "考试ID不能为空")
    private String examId;

    /**
     * 考生姓名，不能为空
     */
//    @NotBlank(message = "考生姓名不能为空")
    private String name;

    /**
     * 试卷代码，不能为空
     */
    @NotBlank(message = "试卷代码不能为空")
    private String paperCode;

    /**
     * 考生ID，不能为空
     */
    @NotBlank(message = "考生ID不能为空")
    private String examineeId;

}