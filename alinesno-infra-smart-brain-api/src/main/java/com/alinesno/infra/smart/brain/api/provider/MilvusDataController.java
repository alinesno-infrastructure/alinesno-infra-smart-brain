package com.alinesno.infra.smart.brain.api.provider;

import com.alinesno.infra.smart.brain.vector.dto.CollectFieldType;
import com.alinesno.infra.smart.brain.vector.dto.InsertField;
import com.alinesno.infra.smart.brain.vector.service.IMilvusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Milvus数据服务控制器，用于定义对Milvus数据库进行操作的REST API接口。
 */
@RestController
@RequestMapping("/api/smart/brain/milvus")
public class MilvusDataController {

    @Autowired
    private IMilvusDataService milvusDataService;

    /**
     * 创建集合的REST API接口
     * @param collectionName 集合名称
     * @param description 集合描述
     * @param shardsNum 分片数量
     * @param fieldType 字段类型
     */
    @PostMapping("/createCollection")
    public void createCollection(@RequestParam String collectionName,
                                 @RequestParam String description,
                                 @RequestParam int shardsNum,
                                 @RequestParam CollectFieldType fieldType) {
        milvusDataService.buildCreateCollectionParam(collectionName, description, shardsNum, fieldType);
    }

    /**
     * 插入数据的REST API接口
     * @param collectionName 集合名称
     * @param partitionName 分区名称
     * @param fields 插入参数字段列表
     */
    @PostMapping("/insertData")
    public void insertData(@RequestParam String collectionName,
                           @RequestParam String partitionName,
                           @RequestBody List<InsertField> fields) {
        milvusDataService.insertData(collectionName, partitionName, fields);
    }

    /**
     * 删除数据的REST API接口
     * @param collectionName 集合名称
     * @param deleteExpr 删除表达式
     */
    @DeleteMapping("/deleteData")
    public void deleteData(@RequestParam String collectionName,
                           @RequestParam String deleteExpr) {
        milvusDataService.deleteData(collectionName, deleteExpr);
    }
}
