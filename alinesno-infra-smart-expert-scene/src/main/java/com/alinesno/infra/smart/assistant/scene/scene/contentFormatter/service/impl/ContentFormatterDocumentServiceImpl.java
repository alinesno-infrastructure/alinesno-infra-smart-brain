package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.DateUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.SmartDocumentConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.DocumentSourceEnums;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterDocumentMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterDocumentService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterOfficeConfigService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.DocxHtmlFormatterUtils;
import com.alinesno.infra.smart.scene.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

/**
 * 描述:文档操作接口
 */
@Slf4j
@Service
public class ContentFormatterDocumentServiceImpl extends IBaseServiceImpl<ContentFormatterDocumentEntity, ContentFormatterDocumentMapper> implements IContentFormatterDocumentService {

    @Autowired
    private IContentFormatterOfficeConfigService officeConfigService;

    @Autowired
    private SmartDocumentConsumer smartDocumentConsumer;

    @Override
    public Long saveUploadDocument(String fileName, File targetFile, long sceneId, PermissionQuery query, String storageId) {

        // fileName去掉后缀
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));

        log.info("上传文件:{}", fileName);

        // 1. 获取办公工具配置
        ContentFormatterOfficeConfigEntity officeConfig = officeConfigService.getConfig(query.getOrgId());
        if (officeConfig == null) {
            throw new RuntimeException("未找到机构[" + query.getOrgId() + "]的办公工具配置");
        }

        String htmlContent =smartDocumentConsumer.convertToHtml(targetFile) ; //.getData() ;

        ContentFormatterDocumentEntity entity = new ContentFormatterDocumentEntity();

        entity.setSceneId(sceneId);
        entity.setDocumentName(fileNameWithoutExtension) ;
        entity.setDocumentDesc(DocxHtmlFormatterUtils.htmlToText(htmlContent , 100));
        entity.setDocumentContent(htmlContent);
        entity.setDocumentSource(DocumentSourceEnums.UPLOAD.getCode());
        entity.setLastSavedTime(new Date());
        entity.setStorageId(storageId);

        // 权限复制
        entity.setOperatorId(query.getOperatorId());
        entity.setOrgId(query.getOrgId());
        entity.setDepartmentId(query.getDepartmentId());

        save(entity) ;

        return entity.getId() ;
    }

    @Override
    public Long saveNewDocument(Long sceneId, PermissionQuery query) {
        ContentFormatterDocumentEntity entity = new ContentFormatterDocumentEntity();

        entity.setSceneId(sceneId);
        entity.setDocumentName("新建文档_" + DateUtils.dateTimeNow());
        entity.setDocumentSource(DocumentSourceEnums.NEW.getCode());
        entity.setLastSavedTime(new Date());

        // 权限复制
        entity.setOperatorId(query.getOperatorId());
        entity.setOrgId(query.getOrgId());
        entity.setDepartmentId(query.getDepartmentId());

        save(entity) ;

        return entity.getId() ;
    }




}