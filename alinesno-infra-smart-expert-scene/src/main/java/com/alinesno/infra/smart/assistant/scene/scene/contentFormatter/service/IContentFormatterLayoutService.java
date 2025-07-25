package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.ContentLayoutExcelParser;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.ContentRuleExcelParser;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterOfficeConfigEntity;

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

}