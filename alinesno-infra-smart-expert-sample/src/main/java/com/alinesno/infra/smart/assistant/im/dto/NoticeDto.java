package com.alinesno.infra.smart.assistant.im.dto;

import lombok.Data;

/**
 * 通知实体
 */
@Data
public class NoticeDto {

    private String taskName ; // 任务名称

    private String businessId ; // 业务标识
    private String usageTime ; // 使用时间
    private String taskStatus ; // 任务状态
    private String finishTime ; // 完成时间
    private String env ;
    private String operator ; // 执行人
    private String applyLink ; // 审批时间

    // 机器人相关信息
    private String chatbotUserId ; // 机器人ID
    private String sender ; // 发起人，用于完成之后@通知对方
    private String senderNick ; // 发起人昵称

}
