package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterDocumentEntity;

import java.io.File;

/**
 * 文档服务接口
 */
public interface IContentFormatterDocumentService extends IBaseService<ContentFormatterDocumentEntity> {

    /**
     * 保存上传的文档
     *
     * @param fileName
     * @param targetFile
     * @param sceneId
     * @param query
     * @param storageId
     * @return
     */
    Long saveUploadDocument(String fileName, File targetFile, long sceneId, PermissionQuery query, String storageId);

    /**
     * 创建新的文档
     * @param sceneId
     * @param query
     * @return
     */
    Long saveNewDocument(Long sceneId, PermissionQuery query);

}