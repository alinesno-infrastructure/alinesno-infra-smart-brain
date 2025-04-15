package com.alinesno.infra.smart.assistant.template.service;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.template.dto.TemplateParamJsonRequestDto;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface ITemplateService extends IBaseService<TemplateEntity> {

    /**
     * 解析模板
     * @param templateKey 模板key
     * @param params 模板参数
     * @return 返回oss路径
     */
    String parseTemplate(String templateKey , Map<String , Object> params) ;

    /**
     * 更新模板参数格式
     * @param dto
     */
    void updateTemplateParamFormat(TemplateParamJsonRequestDto dto);

    /**
     * 获取到当前组织下的所有模板和公共的模板
     * @param query
     * @return
     */
    List<TemplateEntity> getTemplates(PermissionQuery query);

    /**
     * 生成模板
     * @param dataObject
     * @param templateId
     * @return
     */
    String genTemplate(JSONObject dataObject, String templateId);
}
