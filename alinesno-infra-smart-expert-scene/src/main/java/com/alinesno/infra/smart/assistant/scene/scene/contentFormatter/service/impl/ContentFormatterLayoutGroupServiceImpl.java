package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterLayoutGroupMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterOfficeConfigMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterLayoutGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterOfficeConfigService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutGroupEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterOfficeConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContentFormatterLayoutGroupServiceImpl extends IBaseServiceImpl<ContentFormatterLayoutGroupEntity, ContentFormatterLayoutGroupMapper> implements IContentFormatterLayoutGroupService {

}