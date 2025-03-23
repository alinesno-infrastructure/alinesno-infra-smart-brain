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

    private long fileId; /** 文件ID */

    private long length; /** 文件长度 */

    private String fileName; /** 文件名 */

    private String fileType; /** 文件类型 */

    private int wordCount ; /** 文字数量 */

    private int order; /** 附件顺序 */

}
