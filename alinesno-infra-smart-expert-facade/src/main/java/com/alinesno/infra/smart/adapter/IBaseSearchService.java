package com.alinesno.infra.smart.adapter;

import com.alinesno.infra.smart.adapter.dto.MemoryMessageDto;

import java.util.List;

public interface IBaseSearchService {

    /**
     * 获取频道(场景)知识库
     * @param channelId
     * @return
     */
    List<String> getChannelKnowledge(String channelId , String searchText);

    List<String> getChannelKnowledgeByKnowledgeId(String knowledgeId, String searchText);

    /**
     * 获取角色知识库
     * @param roleId
     * @return
     */
    List<String> getRoleKnowledge(String roleId , String searchText);

    /**
     * 获取角色记忆能力
     * @param roleId  角色ID
     * @param targetId 角色记忆目标ID
     * @param memoryText 角色记忆信息
     * @return
     */
    List<String> getRoleMemory(String roleId , String targetId , String memoryText);

    /**
     * 保存角色记忆能力
     * @param roleId
     * @param memoryData
     */
    void saveRoleMemory(String roleId , String roleName , List<MemoryMessageDto> memoryData) ;

}
