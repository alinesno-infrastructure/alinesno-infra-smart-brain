package com.alinesno.infra.base.search.vector.service;


import com.alinesno.infra.base.search.vector.dto.EmbeddingBean;

import java.util.List;


/**
 * Milvus数据服务接口，定义了对Milvus数据库进行操作的方法。
 */
public interface IMilvusDataService {

    /**
     * 构建创建集合参数。
     * @param collectionName 集合名称。
     * @param description 集合描述。
     * @param shardsNum 分片数量。
     */
    void buildCreateCollectionParam(String collectionName, String description, int shardsNum);

    /**
     * 插入数据到集合中。
     * @param collectionName 集合名称。
     * @param partitionName 分区名称。
     * @param embeddingBean 插入参数字段列表。
     */
    void insertData(String collectionName, String partitionName, EmbeddingBean embeddingBean);

    /**
     * 删除集合中的数据。
     * @param collectionName 集合名称。
     * @param deleteExpr 删除表达式。
     */
    void deleteData(String collectionName, String deleteExpr);

    /**
     * 文本转换成向量
     * @param searchText
     * @return
     */
    List<List<Float>> textToVector(String searchText);

    /**
     * 创建索引服务
     * @param collectionName
     */
    void buildIndexByCollection(String collectionName);
}
