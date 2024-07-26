package com.alinesno.infra.smart.assistant.im.dingtalk.dto;

import lombok.Data;

@Data
public class DingtalkRobotMessageDto {

    private String webhook ;  // 加签密钥
    private String secret ; // webhook 地址
    private String messageType ; // 消息类型
    private String messageContent ; // 消息内容
    private String atUser ; // @人员
    private boolean atAll ; // @所有人

}
