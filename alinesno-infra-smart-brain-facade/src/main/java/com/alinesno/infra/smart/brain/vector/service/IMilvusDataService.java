package com.alinesno.infra.smart.brain.vector.service;


import com.alinesno.infra.smart.brain.api.PDFDataDto;
import com.alinesno.infra.smart.brain.vector.dto.CollectFieldType;
import com.alinesno.infra.smart.brain.vector.dto.InsertField;

import java.io.InputStream;
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
     * @param fieldType 字段类型。
     */
    void buildCreateCollectionParam(String collectionName, String description, int shardsNum, CollectFieldType fieldType);

    /**
     * 插入数据到集合中。
     * @param collectionName 集合名称。
     * @param partitionName 分区名称。
     * @param fields 插入参数字段列表。
     */
    void insertData(String collectionName, String partitionName, List<InsertField> fields);

    /**
     * 删除集合中的数据。
     * @param collectionName 集合名称。
     * @param deleteExpr 删除表达式。
     */
    void deleteData(String collectionName, String deleteExpr);

    /**
     * 调用python的M3E接口服务，返回问句的向量化数据
     * @param msg
     */
    void doEmbedding(String msg) ;

    void save(List<String> sentenceList);

}
