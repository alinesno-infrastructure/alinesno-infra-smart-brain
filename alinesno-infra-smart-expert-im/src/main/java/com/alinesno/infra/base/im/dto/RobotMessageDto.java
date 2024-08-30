package com.alinesno.infra.base.im.dto;

import lombok.Data;

@Data
public class RobotMessageDto {

    private String webhook ;  // 加签密钥
    private String secret ; // webhook 地址
    private String messageType ; // 消息类型
    private String messageContent ; // 消息内容
    private String atUser ; // @人员
    private boolean atAll ; // @所有人

}
