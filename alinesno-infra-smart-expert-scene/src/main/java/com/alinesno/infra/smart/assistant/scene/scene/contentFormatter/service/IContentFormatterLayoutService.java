package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.DocumentFormatDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.ContentLayoutExcelParser;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutEntity;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface IContentFormatterLayoutService extends IBaseService<ContentFormatterLayoutEntity> {

    /**
     * 读取Excel
     * @param templates
     * @param query
     * @return
     */
    Map<String, Object> readExcel(List<ContentLayoutExcelParser.LayoutBean> templates, PermissionQuery query);

    /**
     * 根据分组ID查询
     * @param groupIds
     * @return
     */
    List<ContentFormatterLayoutEntity> listByGroupIds(List<Long> groupIds);

    /**
     * 内容格式化
     * @param content
     * @return
     */
    String formatContent(DocumentFormatDTO content, PermissionQuery query);

}