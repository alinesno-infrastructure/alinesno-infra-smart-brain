package com.alinesno.infra.smart.im.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * MessageExportWordDto
 */
@Data
public class MessageExportWordDto {

    /**
     * 消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    /**
     * 消息内容
     */
    @NotNull(message = "消息内容不能为空")
    private String content ;

    /**
     * 下载文档名称（可选）
     */
    private String documentName;

}
