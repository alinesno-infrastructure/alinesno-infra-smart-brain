package com.alinesno.infra.smart.assistant.adapter.service;

import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.adapter.dto.MemoryMessageDto;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.dtflys.forest.callback.OnProgress;

import java.util.List;
import java.util.Map;

/**
 * 基础的搜索管理服务
 */
public interface BaseSearchConsumer {

   /**
    * 上传文件到知识库
    * @param filePath
    * @param datasetId
    * @param onProgress
    * @return
    */
   R<String> datasetUpload(
           String filePath ,
           String datasetId  ,
           OnProgress onProgress) ;

   /**
    * 创建知识库数据集
    */
   R<Long> datasetCreate(
           String datasetDesc ,
           String datasetName ,
           String orgId ,
           String operatorId) ;

   /**
    * 查询数据集
    */
   R<List<DocumentVectorBean>> datasetSearch(
           VectorSearchDto topK) ;

   /**
    * 分页查询数据集
    */
   TableDataInfo datasetSearchPage(
           String datasetId ,
           int pageNum ,
           int pageSize) ;

   /**
    * 保存智能体记忆（批量）
    */
   R<String> memoryAddBatch(
           String agentId,
           String agentName,
           List<MemoryMessageDto> memories
   );

   /**
    * 检索智能体记忆
    */
   R<List<Map<String, Object>>> memoryQuery(
           String agentId,
           String targetId,
           String memoryText
   );

}
