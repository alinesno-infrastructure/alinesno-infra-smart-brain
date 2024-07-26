package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.RoleChainEntity;
import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;

import java.util.Map;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IIndustryRoleService extends IBaseService<IndustryRoleEntity> {

    /**
     * 保存角色工作流信息
     *
     * @param entity
     * @param roleId
     */
    void saveRoleChainInfo(RoleChainEntity entity, String roleId);

    /**
     * 运行角色工作流
     * @param params
     * @param roleId
     */
    void runRoleChainByRoleId(Map<String , Object> params , long roleId , NoticeDto noticeDto);

    /**
     * 运行下一个角色业务
     * @param businessId
     * @param roleId
     */
    void runChainAgent(TaskContentDto businessId, long roleId);
}