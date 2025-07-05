package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 考试页面提交数据DTO
 */
@Data
public class ExamPageSubmitDto {

    /**
     * 试题列表（JSON数组格式）
     * 必须为非空
     */
    @NotNull(message = "试题列表不能为空")
    private JSONArray questions;

    /**
     * 考生答案（JSON对象格式）
     * 必须为非空
     */
    @NotNull(message = "考生答案不能为空")
    private JSONObject answers;

    /**
     * 考生账号ID
     * 必须为非空字符串
     */
    @NotBlank(message = "考生账号ID不能为空")
    private String accountId;

    /**
     * 考试ID
     * 必须为非空字符串
     */
    @NotBlank(message = "考试ID不能为空")
    private String examId;

    /**
     * 提交时间（建议格式：yyyy-MM-dd HH:mm:ss）
     */
    @NotBlank(message = "提交时间不能为空")
    private String submitTime;

    /**
     * 提交类型（如：normal-正常提交，auto-自动提交）
     */
    @NotBlank(message = "提交类型不能为空")
    private String submitType;
}