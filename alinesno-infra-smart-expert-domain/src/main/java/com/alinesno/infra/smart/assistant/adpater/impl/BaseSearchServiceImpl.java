package com.alinesno.infra.smart.assistant.adpater.impl;

import com.alinesno.infra.smart.adapter.IBaseSearchService;
import com.alinesno.infra.smart.adapter.dto.MemoryMessageDto;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.service.IChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * BaseSearchServiceImpl
 */
@Slf4j
@Service
public class BaseSearchServiceImpl implements IBaseSearchService {

    @Autowired
    private BaseSearchConsumer baseSearchConsumer;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Autowired
    private IChannelService channelService;

    /**
     * 获取知识库
     * @param channelId
     * @return
     */
    @Override
    public List<String> getChannelKnowledge(String channelId , String searchText) {

        ChannelEntity channelEntity =  channelService.getById(channelId) ;
        if (channelEntity != null && channelEntity.getKnowledgeId() != null) {
           return getChannelKnowledgeByKnowledgeId(channelEntity.getKnowledgeId() ,searchText) ;
        }

        return null;
    }

    /**
     * 获取知识库
     * @param knowledgeId
     * @return
     */
    @Override
    public List<String> getChannelKnowledgeByKnowledgeId(String knowledgeId, String searchText) {

        VectorSearchDto vectorSearchDto = new VectorSearchDto() ;
        vectorSearchDto.setDatasetId(Long.parseLong(knowledgeId)) ;
        vectorSearchDto.setSearchText(searchText) ;
        vectorSearchDto.setTopK(5) ;

        List<DocumentVectorBean> dataSearchList = baseSearchConsumer.datasetSearch(vectorSearchDto).getData() ;
        return dataSearchList.stream().map(DocumentVectorBean::getDocument_content).toList() ;
    }

    /**
     * 获取角色知识库
     * @param roleId
     * @return
     */
    @Override
    public List<String> getRoleKnowledge(String roleId , String searchText) {

        IndustryRoleEntity roleEntity =  industryRoleService.getById(roleId) ;

        VectorSearchDto vectorSearchDto = new VectorSearchDto() ;
//        vectorSearchDto.setDatasetId(Long.parseLong(roleEntity.getKnowledgeId())) ;
        vectorSearchDto.setSearchText(searchText) ;
        vectorSearchDto.setTopK(5) ;

        List<DocumentVectorBean> dataSearchList = baseSearchConsumer.datasetSearch(vectorSearchDto).getData() ;
        return dataSearchList.stream().map(DocumentVectorBean::getDocument_content).toList() ;
    }

    /**
     * 获取角色记忆
     * @param roleId
     * @return
     */
    @Override
    public List<String> getRoleMemory(String roleId , String targetId , String memoryText) {
        List<Map<String, Object>> memoryData =  baseSearchConsumer.memoryQuery(roleId,targetId,memoryText).getData() ;
        return memoryData.stream().map(map -> map.get("content").toString() + ":" + map.get("keys")).toList();
    }

    /**
     * 保存角色记忆能力
     * @param roleId
     * @param roleName
     * @param memoryData
     */
    @Override
    public void saveRoleMemory(String roleId, String roleName, List<MemoryMessageDto> memoryData) {
        String result = baseSearchConsumer.memoryAddBatch(roleId,roleName,memoryData).getData() ;
        log.debug("保存角色记忆能力：{}" , result);
    }

}
