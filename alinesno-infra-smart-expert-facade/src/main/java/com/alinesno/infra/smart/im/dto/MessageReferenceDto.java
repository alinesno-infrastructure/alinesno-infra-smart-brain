package com.alinesno.infra.smart.im.dto;

import lombok.Data;

/**
 * 消息来源及引用类
 */
@Data
public class MessageReferenceDto {

    private String id;  // 引用id
    private String documentName ;  // 文档名称
    private String documentUrl ; // 文档链接

}
