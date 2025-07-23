package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterOfficeConfigMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterParseMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterOfficeConfigService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterParseService;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterOfficeConfigEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterParseEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ContentFormatterOfficeConfigServiceImpl extends IBaseServiceImpl<ContentFormatterOfficeConfigEntity, ContentFormatterOfficeConfigMapper> implements IContentFormatterOfficeConfigService {

    @Override
    public ContentFormatterOfficeConfigEntity getConfig(Long orgId) {
        LambdaQueryWrapper<ContentFormatterOfficeConfigEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ContentFormatterOfficeConfigEntity::getOrgId, orgId);
        return mapper.selectOne(lambdaQueryWrapper);
    }

}