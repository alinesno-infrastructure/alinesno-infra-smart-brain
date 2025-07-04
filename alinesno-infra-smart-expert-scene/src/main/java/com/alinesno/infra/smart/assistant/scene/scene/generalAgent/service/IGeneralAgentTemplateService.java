package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service;


import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.tools.GeneralRobustExcelParser;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTemplateEntity;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IGeneralAgentTemplateService extends IBaseService<GeneralAgentTemplateEntity> {

    /**
     * 根据类型获取模板
     *
     * @param query
     * @param typeId
     * @return
     */
    List<GeneralAgentTemplateEntity> getTemplateByType(PermissionQuery query, String typeId);

    /**
     * 读取Excel数据，并保存模板
     *
     * @param dataList
     * @param query
     * @return
     */
    Map<String, Object> readExcel(List<GeneralRobustExcelParser.TemplateBean> dataList, PermissionQuery query);

}
