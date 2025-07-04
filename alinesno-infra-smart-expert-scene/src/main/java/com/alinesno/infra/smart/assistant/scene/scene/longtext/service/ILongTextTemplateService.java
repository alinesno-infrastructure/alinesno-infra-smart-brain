package com.alinesno.infra.smart.assistant.scene.scene.longtext.service;


import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.tools.RobustExcelParser;
import com.alinesno.infra.smart.scene.entity.LongTextTemplateEntity;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface ILongTextTemplateService extends IBaseService<LongTextTemplateEntity> {

    /**
     * 根据类型获取模板
     *
     * @param query
     * @param typeId
     * @return
     */
    List<LongTextTemplateEntity> getTemplateByType(PermissionQuery query, String typeId);

    /**
     * 读取Excel数据，并保存模板
     *
     * @param dataList
     * @param query
     * @return
     */
    Map<String, Object> readExcel(List<RobustExcelParser.TemplateBean> dataList, PermissionQuery query);

}
