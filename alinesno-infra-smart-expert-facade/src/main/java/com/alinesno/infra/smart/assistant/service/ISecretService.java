package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.SecretEntity;

import java.util.Map;

public interface ISecretService extends IBaseService<SecretEntity> {

    /**
     * 通过组织获取密钥
     * @param orgId
     * @return
     */
    Map<String, String> getByOrgId(Long orgId);

}
