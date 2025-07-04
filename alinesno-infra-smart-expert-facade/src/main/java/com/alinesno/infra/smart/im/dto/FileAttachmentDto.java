package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

/**
 * 聊天消息中的文件列表
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    /**
     * 任务信息
     */
    private MessageTaskInfo taskInfo ;

    /**
     * 角色信息
     */
    private IndustryRoleEntity role ;

    /**
     * 一对一聊天 ID
     */
    private String oneChatId ;

    /**
     * 已经传递指定的文件
     */
    private File file ;

}