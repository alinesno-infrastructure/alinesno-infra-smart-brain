package com.alinesno.infra.smart.im.dto;

import lombok.Data;

/**
 * 步骤执行状态信息
 * 该类用于封装步骤的执行状态信息，包括步骤ID、执行状态和消息信息
 */
@Data
public class FlowStepStatusDto {

    /**
     * 步骤ID
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
    private String flowChatText;

    /**
     * 推理消息
     */
    private String flowReasoningText;

    /**
     * 是否打印聊天文本
     */
    private boolean isPrint;

    public String getFlowChatText() {
        if (flowChatText == null || flowChatText.isEmpty() || "null".equals(flowChatText)) {
            return "";
        }
        return flowChatText;
    }
}
