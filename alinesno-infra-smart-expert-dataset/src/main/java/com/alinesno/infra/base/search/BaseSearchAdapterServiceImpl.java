package com.alinesno.infra.base.search;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.search.entity.VectorDatasetEntity;
import com.alinesno.infra.base.search.enums.FileTypeEnums;
import com.alinesno.infra.base.search.gateway.utils.CollectionUtils;
import com.alinesno.infra.base.search.service.IDatasetKnowledgeService;
import com.alinesno.infra.base.search.service.IDocumentParserService;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.adapter.dto.MemoryMessageDto;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtflys.forest.callback.OnProgress;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * BaseSearchServiceImpl
 */
@Slf4j
@Service
public class BaseSearchAdapterServiceImpl implements BaseSearchConsumer {

    @Autowired
    private IDocumentParserService documentParserService ;

    @Resource
    private IVectorDatasetService vectorDatasetService;

    @Autowired
    private IDatasetKnowledgeService service;

    @SneakyThrows
    @Override
    public R<String> datasetUpload(String filePath, String datasetId, OnProgress onProgress) {

        // 获取原始文件名
        String fileName = new File(filePath).getName();
        List<String> sentenceList = new ArrayList<>();

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(filePath) ;

        String fileType = FileTypeUtil.getType(fileName);

        FileTypeEnums constants = FileTypeEnums.getByValue(fileSuffix.toLowerCase()) ;
        assert constants != null;

        sentenceList = switch (constants) {
            case TXT ->  documentParserService.parseTxt(targetFile);
            case PDF ->  documentParserService.parsePDF(targetFile);
            case MD -> documentParserService.parseMD(targetFile);
            case EXCEL -> documentParserService.parseExcel(targetFile);
            case DOCX -> documentParserService.getContentDocx(targetFile);
            case DOC -> documentParserService.getContentDoc(targetFile);
            default -> sentenceList;
        };

        log.debug("sentenceList = {}" , new Gson().toJson(sentenceList));

        // 处理完成之后删除文件
        FileUtils.forceDeleteOnExit(targetFile);

        if(sentenceList.isEmpty()){
            return R.fail(fileName + " 文档解析失败.") ;
        }

        service.parserDocument(Long.valueOf(datasetId), sentenceList, fileName , fileType);

        return R.ok("上传成功",fileName) ;
    }

    public R<Long> datasetCreate(String datasetDesc, String datasetName , String orgId , String operatorId) {
        log.debug("datasetName = {} , datasetDesc = {}" , datasetName , datasetDesc);

        if(orgId == null || operatorId == null){
            log.warn("参数orgId组织或者operatorId个人信息");
        }

        String collectionName = CollectionUtils.getCollectionName() ;

        VectorDatasetEntity entity = getVectorDatasetEntity(datasetName, collectionName , datasetDesc , orgId , operatorId);
        return R.ok(entity.getId()) ;
    }

    /**
     * 获取到数据集
     *
     * @param datasetName
     * @param collectionName
     * @param datasetDesc
     * @param orgIdStr
     * @param operatorIdStr
     * @return
     */
    private VectorDatasetEntity getVectorDatasetEntity(String datasetName,
                                                       String collectionName ,
                                                       String datasetDesc, String orgIdStr, String operatorIdStr) {

        LambdaQueryWrapper<VectorDatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VectorDatasetEntity::getCollectionName, collectionName);

        // 如果已经存在则直接返回entity
        if(vectorDatasetService.count(queryWrapper) > 0){
            return vectorDatasetService.getOne(queryWrapper);
        }

        VectorDatasetEntity entity = new VectorDatasetEntity() ;
        entity.setName(datasetName);
        entity.setDescription(datasetDesc);

        String description = entity.getDescription() ;
        int shardsNum = 1 ;

        entity.setDatasetSize(0);
        entity.setAccessPermission("person");
        entity.setCollectionName(collectionName);

        long ownerId = operatorIdStr == null ? 0L : Long.parseLong(operatorIdStr);
        entity.setOrgId(orgIdStr == null?0L:Long.parseLong(orgIdStr));
        entity.setOwnerId(ownerId);
        entity.setOperatorId(ownerId);

        // vectorDatasetService.buildCreateCollectionParam(collectionName, description, shardsNum);

        log.debug("buildCreateCollectionParam = " + collectionName);
        vectorDatasetService.save(entity);
        return entity;
    }

    @Override
    public R<List<DocumentVectorBean>> datasetSearch(VectorSearchDto dto) {
        Long datasetId = dto.getDatasetId() ;
        VectorDatasetEntity datasetEntity = vectorDatasetService.getById(datasetId) ;
        dto.setCollectionName(datasetEntity.getCollectionName());

        List<DocumentVectorBean> topksList = vectorDatasetService.search(dto);
        return R.ok(topksList);
    }

    @Override
    public TableDataInfo datasetSearchPage(String datasetId, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public R<String> memoryAddBatch(String agentId, String agentName, List<MemoryMessageDto> memories) {
        return null;
    }

    @Override
    public R<List<Map<String, Object>>> memoryQuery(String agentId, String targetId, String memoryText) {
        return null;
    }
}
