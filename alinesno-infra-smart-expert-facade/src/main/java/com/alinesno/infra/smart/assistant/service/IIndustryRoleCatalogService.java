package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.IndustryRoleCatalogDto;
import com.alinesno.infra.smart.assistant.api.TreeSelectDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleCatalogEntity;

import java.util.List;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IIndustryRoleCatalogService extends IBaseService<IndustryRoleCatalogEntity> {

    /**
     * 查询出指令类型列表
     *
     * @param promptCatalog
     * @param query
     * @return
     */
    List<IndustryRoleCatalogEntity> selectCatalogList(IndustryRoleCatalogEntity promptCatalog, PermissionQuery query);

    /**
     * 保存用户类型
     * @param entity
     */
    void insertCatalog(IndustryRoleCatalogEntity entity);

    /**
     * 查询类型列表树
     * @return
     */
    List<TreeSelectDto> selectCatalogTreeList(PermissionQuery query);

    /**
     * 列出所有的子类型
     * @return
     */
    List<IndustryRoleCatalogDto> allCatalog(PermissionQuery query);

    /**
     * 获取默认的角色团队类型
     * @return
     */
    IndustryRoleCatalogEntity getDefaultCatalog(IndustryRoleCatalogEntity entity);
}