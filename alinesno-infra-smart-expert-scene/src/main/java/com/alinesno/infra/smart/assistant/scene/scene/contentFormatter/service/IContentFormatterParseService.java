package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterParseEntity;

import java.util.List;

public interface IContentFormatterParseService extends IBaseService<ContentFormatterParseEntity> {

    /**
     * 获取计划树
     * @param id
     * @param id1
     * @return
     */
    List<TreeNodeDto> getPlanTree(Long id, Long id1);
}