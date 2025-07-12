package com.alinesno.infra.smart.assistant.role.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.alinesno.infra.base.search.service.reader.*;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IAttachmentReaderService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 附件内容读取工具
 */
@Component
public class AttachmentReaderUtils {


    /**
     * 读取附件列表，并解析出每个附件的内容出来
     *
     * @param attachments
     * @param uploadData
     * @param taskInfo
     * @param role
     * @param oneChatId
     * @return
     */
    public List<FileAttachmentDto> readAttachmentList(List<FileAttachmentDto> attachments,
                                                      UploadData uploadData,
                                                      MessageTaskInfo taskInfo,
                                                      IndustryRoleEntity role,
                                                      String oneChatId){

        List<FileAttachmentDto> newAttachments = new ArrayList<>() ;

        // 解析内容并过滤为空的数据
        for(FileAttachmentDto attachment : attachments){

            String fileType = attachment.getFileType() ;
            attachment.setRole(role);
            attachment.setTaskInfo(taskInfo);
            attachment.setOneChatId(oneChatId);

            IAttachmentReaderService attachmentReaderService = getAttachmentReaderService(fileType) ;
            if(attachmentReaderService != null){
                String fileContent = attachmentReaderService.readAttachment(attachment , uploadData) ;

                if(fileContent != null && !fileContent.isEmpty()){
                    attachment.setFileContent(fileContent);
                    newAttachments.add(attachment) ;
                }
            }
        }

        return newAttachments ;
    }

    public IAttachmentReaderService getAttachmentReaderService(String fileType) {
        return switch (fileType) {
            case "txt" -> SpringUtil.getBean(TextReaderServiceImpl.class);
            case "md" -> SpringUtil.getBean(MarkdownReaderServiceImpl.class);
            case "ppt", "pptx" -> SpringUtil.getBean(PPTReaderServiceImpl.class);
            case "pdf" -> SpringUtil.getBean(PdfReaderServiceImpl.class);
            case "doc", "docx" -> SpringUtil.getBean(WordReaderServiceImpl.class);
            case "xls", "xlsx" -> SpringUtil.getBean(ExcelReaderServiceImpl.class);
            case "png", "jpg", "jpeg" -> SpringUtil.getBean(ImageReaderServiceImpl.class);
            default -> null;
        };
    }
}
