package com.alinesno.infra.base.im.dto;

import lombok.Data;

@Data
public class MessageQueueDto {

    private long id ;
    private String businessId ;
    private String status ;
    private Integer priority;  // 消息优先级
    private Integer sequence;  // 消息在队列中的序列号
    private long agentId ; // AgentId
    private long channelId ; // channelId
    private String content;  // 消息内容(使用的是Map-JSON格式化)
    private String assistantContent ;  // 生成的内容

    // 生成的信息内容(这里结合查看服务)
    private String linkType ; // 连接类型
    private String linkPath ; // 连接地址

}
