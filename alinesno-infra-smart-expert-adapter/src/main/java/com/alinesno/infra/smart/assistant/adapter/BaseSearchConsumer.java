package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.adapter.dto.MemoryMessageDto;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorDto;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnProgress;

import java.util.List;
import java.util.Map;

/**
 * 基础的搜索管理服务
 */
@BaseRequest(baseURL = "#{alinesno.infra.gateway.host}/base-search" , connectTimeout = 30*1000)
public interface BaseSearchConsumer {

   /**
    * 上传文件到知识库
    * @param filePath
    * @param datasetId
    * @param onProgress
    * @return
    */
   @Post(url = "/api/base/search/vectorData/upload" , contentType = "multipart/form-data")
   R<String> datasetUpload(
           @DataFile("file") String filePath ,
           @Body("datasetId") String datasetId  ,
           OnProgress onProgress) ;

   /**
    * 创建知识库数据集
    */
   @Post(url = "/api/base/search/vectorData/createDataset")
   R<String> datasetCreate(
           @Query("datasetDesc") String datasetDesc ,
           @Query("datasetName") String datasetName) ;

   /**
    * 查询数据集
    */
   @Post(url = "/api/base/search/vectorSearch/search")
   R<List<DocumentVectorDto>> datasetSearch(
           @JSONBody VectorSearchDto topK) ;

   /**
    * 分页查询数据集
    */
   @Post(url = "/api/infra/base/search/datasetKnowledge/datatables")
   TableDataInfo datasetSearchPage(
           @Query("datasetId") String datasetId ,
           @Query("pageNum") int pageNum ,
           @Query("pageSize") int pageSize) ;

   /**
    * 保存智能体记忆（批量）
    */
   @Put(url = "/api/base/search/memoryData/addBatch")
   R<String> memoryAddBatch(
           @Query("agentId") String agentId,
           @Query("agentName") String agentName,
           @Body List<MemoryMessageDto> memories
   );

   /**
    * 检索智能体记忆
    */
   @Get(url = "/api/base/search/memoryData/query")
   R<List<Map<String, Object>>> memoryQuery(
           @Query("agentId") String agentId,
           @Query("targetId") String targetId,
           @Query("memoryText") String memoryText
   );

}
