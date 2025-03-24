package com.alinesno.infra.smart.im.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天消息中的文件列表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileAttachmentDto {

    /**
     * 文件 ID
     */
    private long fileId;

    /**
     * 文件长度
     */
    private long length;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件内容
     */
    private String fileContent = "" ;

    /**
     * 文字数量
     */
    private int wordCount;

    /**
     * 附件顺序
     */
    private int order;

}