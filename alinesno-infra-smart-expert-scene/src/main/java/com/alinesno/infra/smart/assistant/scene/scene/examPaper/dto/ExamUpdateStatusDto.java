package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 考试更新状态数据传输对象
 */
@Data
public class ExamUpdateStatusDto {

    @NotNull(message = "考试ID不能为空")
    private Long examId ;

    @NotNull(message = "考生ID不能为空")
    private Long examineeId ;

    @NotNull(message = "状态不能为空")
    private String status ;

}
