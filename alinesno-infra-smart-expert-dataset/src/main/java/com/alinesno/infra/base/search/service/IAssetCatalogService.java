package com.alinesno.infra.base.search.service;


import com.alinesno.infra.base.search.api.TreeSelectDto;
import com.alinesno.infra.base.search.entity.AssetCatalogEntity;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;

import java.util.List;

/**
 * 资产编目Service接口
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
public interface IAssetCatalogService extends IBaseService<AssetCatalogEntity> {

    /**
     * 查询出指令类型列表
     *
     * @param promptCatalog
     * @param query
     * @return
     */
    List<AssetCatalogEntity> selectCatalogList(AssetCatalogEntity promptCatalog, PermissionQuery query);

    /**
     * 保存用户类型
     * @param entity
     */
    void insertCatalog(AssetCatalogEntity entity);

    /**
     * 查询类型列表树
     * @return
     */
    List<TreeSelectDto> selectCatalogTreeList(PermissionQuery query , String type);

    /**
     * 查询顶级类型列表
     *
     * @param count
     * @param query
     * @return
     */
    List<AssetCatalogEntity> topCatalog(int count, PermissionQuery query);

    /**
     * 查询清单列表信息
     * @param query
     * @param type 类型(向量、索引)
     * @return
     */
    List<TreeSelectDto> catalogManifestTreeSelect(PermissionQuery query , String type);
}
