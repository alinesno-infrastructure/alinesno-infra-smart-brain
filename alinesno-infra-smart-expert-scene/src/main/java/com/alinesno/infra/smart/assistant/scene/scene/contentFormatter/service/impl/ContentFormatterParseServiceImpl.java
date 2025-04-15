package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterParseMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterParseService;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterParseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ContentFormatterParseServiceImpl extends IBaseServiceImpl<ContentFormatterParseEntity , ContentFormatterParseMapper> implements IContentFormatterParseService {

    @Override
    public List<TreeNodeDto> getPlanTree(Long id, Long id1) {
        return null;
    }
}