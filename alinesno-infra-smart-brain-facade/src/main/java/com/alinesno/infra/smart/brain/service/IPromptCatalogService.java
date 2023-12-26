package com.alinesno.infra.smart.brain.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.brain.api.TreeSelectDto;
import com.alinesno.infra.smart.brain.entity.PromptCatalogEntity;

import java.util.List;

/**
 * Prompt指令类型Service接口
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IPromptCatalogService extends IBaseService<PromptCatalogEntity> {

    /**
     * 查询出指令类型列表
     * @param promptCatalog
     * @return
     */
    List<PromptCatalogEntity> selectCatalogList(PromptCatalogEntity promptCatalog);

    /**
     * 保存用户类型
     * @param entity
     */
    void insertCatalog(PromptCatalogEntity entity);

    /**
     * 查询类型列表树
     * @return
     */
    List<TreeSelectDto> selectCatalogTreeList();
}