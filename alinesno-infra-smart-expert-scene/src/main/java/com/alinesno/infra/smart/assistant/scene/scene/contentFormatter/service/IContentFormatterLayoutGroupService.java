package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutGroupEntity;

import java.util.List;

public interface IContentFormatterLayoutGroupService extends IBaseService<ContentFormatterLayoutGroupEntity> {

    /**
     * 删除分组
     *
     * @param id
     * @param groupType
     */
    void removeGroup(Long id, String groupType);

    /**
     * 查询组织分组
     * @param query
     * @return
     */
    List<ContentFormatterLayoutGroupEntity> listOrgGroup(PermissionQuery query);
}