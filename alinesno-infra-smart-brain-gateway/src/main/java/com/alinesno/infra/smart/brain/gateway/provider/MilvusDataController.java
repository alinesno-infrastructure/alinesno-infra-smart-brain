package com.alinesno.infra.smart.brain.gateway.provider;

import com.alinesno.infra.smart.brain.constants.BrainConstants;
import com.alinesno.infra.smart.brain.utils.parse.PdfParseUtil;
import com.alinesno.infra.smart.brain.utils.parse.WordParseUtil;
import com.alinesno.infra.smart.brain.utils.parse.XmindUtil;
import com.alinesno.infra.smart.brain.vector.dto.CollectFieldType;
import com.alinesno.infra.smart.brain.vector.dto.InsertField;
import com.alinesno.infra.smart.brain.vector.service.IMilvusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
     * 文件上传，支持PDF、Word、Xmind
     * @param file
     * @throws Exception
     */
    @PostMapping("/upload")
    public void upload(MultipartFile file) throws Exception {
        List<String> sentenceList = new ArrayList<>();
        String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        if (BrainConstants.PDF.equalsIgnoreCase(fileSuffix)){
            sentenceList = PdfParseUtil.parse(file.getInputStream());
        }
        if (BrainConstants.DOCX.equalsIgnoreCase(fileSuffix)){
            sentenceList = WordParseUtil.getContentDocx(file.getInputStream());
        }
        if (BrainConstants.DOC.equalsIgnoreCase(fileSuffix)){
            sentenceList = WordParseUtil.getContentDoc(file.getInputStream());
        }
        if (BrainConstants.XMIND.equalsIgnoreCase(fileSuffix)){
            sentenceList = XmindUtil.xmindToList(file.getInputStream());
        }
        milvusDataService.save(sentenceList);
    }


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
