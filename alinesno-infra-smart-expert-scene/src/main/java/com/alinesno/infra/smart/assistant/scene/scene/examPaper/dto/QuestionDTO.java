package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alibaba.fastjson.JSONObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {
    @NotBlank(message = "考核内容不能为空")
    private String assessmentContent;

    @NotNull(message = "是否必填不能为null")
    private Boolean isRequired;

    @NotNull(message = "分数不能为null")
    @Min(value = 0, message = "分数最小值为0")
    private Integer score;

    @NotBlank(message = "问题内容不能为空")
    private String question;

    @NotBlank(message = "答案解析不能为空")
    private String answerAnalysis;

    @NotBlank(message = "ID不能为null")
    private String id;

    @NotBlank(message = "问题类型不能为空")
    private String type;

    @NotNull(message = "排序字段不能为null")
    @Min(value = 1, message = "排序最小值为1")
    private Integer order;

    private List<JSONObject> blanks;

    private List<JSONObject> answers;
}