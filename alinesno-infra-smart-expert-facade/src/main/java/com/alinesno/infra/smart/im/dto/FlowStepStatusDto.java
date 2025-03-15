package com.alinesno.infra.smart.im.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 步骤执行状态信息
 * 该类用于封装步骤的执行状态信息，包括步骤 ID、执行状态、消息信息、聊天文本、推理消息以及是否打印聊天文本的标志。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowStepStatusDto {

    /**
     * 步骤 ID
     * 用于唯一标识一个步骤
     */
    private String stepId;

    /**
     * 执行状态
     * 描述步骤的执行结果，例如成功或失败
     */
    private String status;

    /**
     * 消息信息
     * 提供关于步骤执行的详细信息，通常用于日志记录或调试
     */
    private String message;

    /**
     * 存储聊天文本的字符串变量
     * 用于记录或处理聊天流程中的文本信息
     */
    private String flowChatText = "";

    /**
     * 推理消息
     */
    private String flowReasoningText = "";

    /**
     * 是否打印聊天文本
     */
    private boolean isPrint;

    /**
     * 检查字符串是否为空或为 "null"
     *
     * @param str 要检查的字符串
     * @return 如果字符串为空或为 "null"，返回 true；否则返回 false
     */
    private boolean isNullOrEmptyOrNullString(String str) {
        return str == null || str.isEmpty() || "null".equals(str);
    }

    /**
     * 获取聊天文本，如果文本为空或为 "null"，则返回空字符串
     *
     * @return 聊天文本
     */
    public String getFlowChatText() {
        return isNullOrEmptyOrNullString(flowChatText) ? "" : flowChatText;
    }

    /**
     * 获取推理消息，如果消息为空或为 "null"，则返回空字符串
     *
     * @return 推理消息
     */
    public String getFlowReasoningText() {
        return isNullOrEmptyOrNullString(flowReasoningText) ? "" : flowReasoningText;
    }
}