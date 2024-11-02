package com.alinesno.infra.smart.assistant.template.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;

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

}
