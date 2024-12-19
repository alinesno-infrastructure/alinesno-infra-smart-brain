package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.enums.HasStatusEnums;
import com.alinesno.infra.smart.assistant.entity.SecretEntity;
import com.alinesno.infra.smart.assistant.mapper.SecretMapper;
import com.alinesno.infra.smart.assistant.service.ISecretService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class SecretServiceImpl extends IBaseServiceImpl<SecretEntity, SecretMapper> implements ISecretService {

    @Override
    public Map<String, String> getByOrgId(Long orgId) {

        LambdaQueryWrapper<SecretEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SecretEntity::getHasStatus , HasStatusEnums.LEGAL.value);
        wrapper.eq(SecretEntity::getOrgId, orgId);

        List<SecretEntity> list = list(wrapper) ;

        if(list != null && !list.isEmpty()) {
            return list.stream().collect(Collectors.toMap(SecretEntity::getSecretName, SecretEntity::getSecretValue));
        }

        return new HashMap<>();
    }
}