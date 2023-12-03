package com.alinesno.infra.smart.brain.gateway.provider;

import com.alinesno.infra.smart.brain.vector.service.IMilvusSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 控制器类用于处理Milvus相关的HTTP请求，并调用对应的Milvus搜索服务进行处理。
 */
@RestController
@RequestMapping("/api/smart/brain/milvus")
public class MilvusSearchController {

    @Autowired
    private IMilvusSearchService milvusService;

    /**
     * 处理搜索Milvus集合的HTTP POST请求，并返回最近邻居的ID列表。
     * @param collectionName 要搜索的集合名称。
     * @param vectors 要搜索的向量列表。
     * @param topK 要返回的最近邻居数量。
     * @return 包含最近邻居ID列表的ResponseEntity对象。
     */
    @PostMapping("/search")
    public ResponseEntity<List<Long>> searchMilvus(
            @RequestParam("collectionName") String collectionName,
            @RequestBody List<List<Float>> vectors,
            @RequestParam("topK") Integer topK) {

        List<Long> topksList = milvusService.search(collectionName, vectors, topK);

        return ResponseEntity.ok(topksList);
    }

    /**
     * 处理带有过滤条件的搜索Milvus集合的HTTP POST请求，并返回最近邻居的ID列表。
     * @param collectionName 要搜索的集合名称。
     * @param vectors 要搜索的向量列表。
     * @param topK 要返回的最近邻居数量。
     * @param exp 搜索表达式。
     * @return 包含最近邻居ID列表的ResponseEntity对象。
     */
    @PostMapping("/searchWithFilter")
    public ResponseEntity<List<Long>> searchMilvusWithFilter(
            @RequestParam("collectionName") String collectionName,
            @RequestBody List<List<Float>> vectors,
            @RequestParam("topK") Integer topK,
            @RequestParam("exp") String exp) {

        List<Long> topksList = milvusService.search(collectionName, vectors, topK, exp);

        return ResponseEntity.ok(topksList);
    }

    /**
     * 处理异步搜索Milvus集合的HTTP POST请求，并返回最近邻居的ID列表。
     * @param collectionName 要搜索的集合名称。
     * @param vectors 要搜索的向量列表。
     * @param partitionList 要包括在搜索中的分区列表。
     * @param topK 要返回的最近邻居数量。
     * @return 包含最近邻居ID列表的ResponseEntity对象。
     * @throws ExecutionException 如果搜索过程中发生执行异常。
     * @throws InterruptedException 如果搜索过程被中断。
     */
    @PostMapping("/searchAsync")
    public ResponseEntity<List<Long>> searchMilvusAsync(
            @RequestParam("collectionName") String collectionName,
            @RequestBody List<List<Float>> vectors,
            @RequestParam("partitionList") List<String> partitionList,
            @RequestParam("topK") Integer topK) throws ExecutionException, InterruptedException {

        List<Long> resultIdsList = milvusService.searchAsync(collectionName, vectors, partitionList, topK);

        return ResponseEntity.ok(resultIdsList);
    }

    /**
     * 处理获取Milvus集合分区列表的HTTP GET请求，并返回分区名称列表。
     * @param collectionName 要获取分区列表的集合名称。
     * @return 包含分区名称列表的ResponseEntity对象。
     */
    @GetMapping("/partitions")
    public ResponseEntity<List<String>> getPartitionsList(
            @RequestParam("collectionName") String collectionName) {

        List<String> partitionList = milvusService.getPartitionsList(collectionName);

        return ResponseEntity.ok(partitionList);
    }
}
