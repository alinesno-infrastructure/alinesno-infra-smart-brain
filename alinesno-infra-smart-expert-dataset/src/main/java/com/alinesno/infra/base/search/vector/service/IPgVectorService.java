package com.alinesno.infra.base.search.vector.service;

import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.pgvector.PGvector;

import java.util.List;

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
}
