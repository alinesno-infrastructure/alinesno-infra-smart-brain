package com.alinesno.infra.base.im.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 消息实体信息
 */
@Data
public class ChatMessageDto {

    private long channelId ; // 所属频道Id

    private String roleType;
    private String icon ;
    private String name;
    private String dateTime ;
    private Object chatText;
    private String readerType ; // 读取方式和类型

    private boolean isLoading = false ; // 是否还在加载中

    @JsonSerialize(using = ToStringSerializer.class)
    private long businessId ; // 生成的唯一业务ID标识

    private String className ; // 返回的样式标识

}
