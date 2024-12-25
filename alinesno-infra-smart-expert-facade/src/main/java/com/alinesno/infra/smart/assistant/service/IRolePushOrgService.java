package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.RolePushOrgEntity;

import java.util.List;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IRolePushOrgService extends IBaseService<RolePushOrgEntity> {

    /**
     * 推送组织角色，当前标识为未录用，如果同一角色，版本号会加1，并且前面的标识为已录用
     */
    void pushOrgRole(long roleId , long orgId);

    /**
     * 录用组织角色，录用之后，原角色信息会被覆盖，然后当前角色标识为已录用
     */
    void acceptOrgRole(long roleId , long orgId);

    /**
     * 查询推送组织角色
     * @param orgId
     * @return
     */
    List<IndustryRoleEntity> queryPushOrgRole(long orgId) ;
}