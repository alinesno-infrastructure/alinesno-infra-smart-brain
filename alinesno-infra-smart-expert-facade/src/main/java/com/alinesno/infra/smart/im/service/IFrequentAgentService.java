package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.IndustryFrequentRoleDto;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.im.entity.AccountHomePageEntity;
import com.alinesno.infra.smart.im.entity.FrequentAgentEntity;
import com.alinesno.infra.smart.im.enums.FrequentTypeEnums;

import java.util.List;

/**
 * 常用Agent记录服务接口
 */
public interface IFrequentAgentService extends IBaseService<FrequentAgentEntity> {

    /**
     * 更新Agent点击次数还有最后更新时间
     */
    boolean updateAgentClickCount(Long accountId, Long id , FrequentTypeEnums type);

    /**
     * 添加常用的Agent角色
     */
    boolean addFrequentAgent(Long accountId, Long agentId , FrequentTypeEnums type);

    /**
     * 删除常用的Agent角色
     */
    boolean deleteFrequentAgent(Long accountId, Long agentId , FrequentTypeEnums type);

    /**
     * 获取用户的常用Agent角色列表
     * @param accountId 用户ID
     * @param count 获取的个数
     */
    List<IndustryFrequentRoleDto> getFrequentAgentList(Long accountId , int count);

}