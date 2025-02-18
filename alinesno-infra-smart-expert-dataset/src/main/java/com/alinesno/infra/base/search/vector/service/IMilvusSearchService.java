package com.alinesno.infra.base.search.vector.service;

import com.alinesno.infra.base.search.vector.dto.EmbeddingBean;
import com.alinesno.infra.base.search.vector.dto.PDFDataDto;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 这个接口表示了一个用于在Milvus集合中搜索向量并获取有关集合及其分区信息的服务。
 */
public interface IMilvusSearchService {


    /**
     * 从向量数据库中搜索
     * @param search_vectors
     * @return
     */
    List<PDFDataDto> search(List<List<Float>> search_vectors);

    /**
     * 在指定的集合中搜索相似向量，并返回前K个最近邻居的ID列表。
     *
     * @param collectionName 要搜索的集合的名称。
     * @param vectors        要搜索的向量列表。
     * @param topK           要检索的最近邻居的数量。
     * @return 一个Long值列表，表示最近邻居的ID。
     */
    List<EmbeddingBean> search(String collectionName, List<List<Float>> vectors, Integer topK);

    /**
     * 在指定的集合中搜索相似向量，并返回前K个最近邻居的ID列表，可以指定搜索表达式。
     * @param collectionName 要搜索的集合的名称。
     * @param vectors 要搜索的向量列表。
     * @param topK 要检索的最近邻居的数量。
     * @param exp 要使用的搜索表达式。
     * @return 一个Long值列表，表示最近邻居的ID。
     */
    List<Long> search(String collectionName, List<List<Float>> vectors, Integer topK, String exp);

    /**
     * 异步搜索指定集合中的相似向量，并返回前K个最近邻居的ID列表，可以指定分区列表。
     * @param collectionName 要搜索的集合的名称。
     * @param vectors 要搜索的向量列表。
     * @param partitionList 要包括在搜索中的分区列表。
     * @param topK 要检索的最近邻居的数量。
     * @return 一个Long值列表，表示最近邻居的ID。
     * @throws ExecutionException 如果搜索过程中发生执行异常。
     * @throws InterruptedException 如果搜索过程被中断。
     */
    List<Long> searchAsync(String collectionName, List<List<Float>> vectors, List<String> partitionList, Integer topK) throws ExecutionException, InterruptedException;

    /**
     * 获取指定集合的分区名称列表。
     * @param collectionName 要获取分区名称的集合的名称。
     * @return 一个String值列表，表示集合中分区的名称。
     */
    List<String> getPartitionsList(String collectionName);

}
