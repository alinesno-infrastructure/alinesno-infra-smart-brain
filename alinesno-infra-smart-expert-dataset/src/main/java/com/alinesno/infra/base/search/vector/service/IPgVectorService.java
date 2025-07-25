package com.alinesno.infra.base.search.vector.service;

import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.pgvector.PGvector;

import java.util.List;
import java.util.Map;

public interface IPgVectorService {

    /**
     * 获取向量
     * @param queryText
     * @return
     */
    PGvector getEmbeddingVector(String queryText) ;

    /**
     * 通过文本查询索引内容
     * @param indexName
     * @param fileName
     * @param queryText
     * @param size
     * @return
     */
    List<DocumentVectorBean> queryDocument(String indexName, String fileName , String queryText , int size) ;

    /**
     * 通过文本查询向量库内容
     * @param indexName
     * @param queryText
     * @param size
     * @return
     */
    List<DocumentVectorBean> queryVectorDocument(String indexName, String queryText , int size) ;

    /**
     * 通过文本查询向量库内容
     * @param dto
     * @return
     */
    List<DocumentVectorBean> queryVectorDocument(VectorSearchDto dto) ;

    /**
     * 创建向量索引库
     * @param indexName
     */
    void createVectorIndex(String indexName);

    /**
     * 插入向量数据
     * @param documentVectorBean
     */
    void insertVector(DocumentVectorBean documentVectorBean);

    /**
     * 更新向量数据
     * @param documentVectorBean
     */
    void updateVector(DocumentVectorBean documentVectorBean) ;

    /**
     * 删除向量索引库
     * @param indexName
     */
    void deleteVectorIndex(String indexName, long documentId);

    /**
     * 通过全文查询向量库内容
     * @param dto
     * @return
     */
    List<DocumentVectorBean> queryFullTextDocument(VectorSearchDto dto);

    /**
     * 通过混合查询向量库内容
     * @param dto
     * @return
     */
    List<DocumentVectorBean> queryHybridDocument(VectorSearchDto dto);

    /**
     * 多数据集查询向量库内容
     * @param datasetIdArr
     * @param searchText
     * @param topK
     * @return
     */
    List<DocumentVectorBean> queryMultiVectorDocument(List<Long> datasetIdArr, String searchText, Integer topK);

    /**
     * 根据数据集ID和文档名称分页查询文档
     * @param params 查询参数
     * @return 文档列表
     */
    List<DocumentVectorBean> queryPageByDatasetIdAndDocumentName(Map<String, Object> params);

    /**
     * 根据数据集ID和文档名称统计总记录数
     * @param datasetId 数据集ID
     * @param documentName 文档名称
     * @return 总记录数
     */
    long countByDatasetIdAndDocumentName(Long datasetId, String documentName);

    /**
     * 根据向量ID获取向量信息
     * @param segmentId
     * @return
     */
    DocumentVectorBean getVectorById(long segmentId);
}
