package com.alinesno.infra.smart.im.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChatSendMessageDto implements Serializable {

    private String message;
    private List<Long> users = new ArrayList<>() ; // Agent用户ID列表
    private List<Long> businessIds = new ArrayList<>() ;  // 业务ID列表
    private List<Long> fileIds = new ArrayList<>(); // 文件ID列表
    private Long channelId;

    private Long accountId ; // 操作账号ID
    private String accountName; // 操作账号名称
    private String accountIcon ; // 操作账号头像
    private String accountEmail ; // 操作账号邮箱

    private String type;

}
