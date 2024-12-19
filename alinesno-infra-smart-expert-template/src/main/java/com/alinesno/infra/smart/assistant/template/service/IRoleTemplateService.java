package com.alinesno.infra.smart.assistant.template.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.template.entity.RoleTemplateEntity;

import java.io.IOException;

/**
 * 项目模块 服务类
 *
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IRoleTemplateService extends IBaseService<RoleTemplateEntity> {

    /**
     * 同步用户插件
     * @param accountId
     * @param gitUrl
     */
    void syncRoleTemplate(Long accountId, String gitUrl) throws IOException;

}
