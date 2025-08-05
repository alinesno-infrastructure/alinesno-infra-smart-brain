package com.alinesno.infra.base.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alinesno.infra.base.search.api.DataProcessingDto;
import com.alinesno.infra.base.search.api.SegmentUpdateDto;
import com.alinesno.infra.base.search.entity.DatasetKnowledgeEntity;
import com.alinesno.infra.base.search.entity.VectorDatasetEntity;
import com.alinesno.infra.base.search.enums.DocumentStatusEnums;
import com.alinesno.infra.base.search.enums.FileTypeEnums;
import com.alinesno.infra.base.search.mapper.DatasetKnowledgeMapper;
import com.alinesno.infra.base.search.service.IDatasetKnowledgeService;
import com.alinesno.infra.base.search.service.IDocumentParserService;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用管理Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class DatasetKnowledgeImpl extends IBaseServiceImpl<DatasetKnowledgeEntity, DatasetKnowledgeMapper> implements IDatasetKnowledgeService {

    @Value("${alinesno.base.search.document.max-segment-length:512}")
    private int maxSegmentLength ;

    @Autowired
    private IDocumentParserService documentParserService ;

    @Autowired
    private IVectorDatasetService vectorDatasetService ;

    @Override
    public void parserDocument(Long datasetId, List<String> sentenceList, String fileName, String fileType) {

        // 从DataSet里面获取到长度
        sentenceList = documentParserService.documentParser(sentenceList.get(0) , maxSegmentLength) ;

        DatasetKnowledgeEntity e = new DatasetKnowledgeEntity() ;
        e.setDocumentName(fileName);
        e.setDatasetId(datasetId);
        e.setDocumentDesc(fileName);
        e.setDocumentCount(sentenceList.size());

        log.debug("document count = {}" , e.getDocumentCount());

        save(e) ;

        if(!sentenceList.isEmpty()){
            sentenceList.forEach(s -> log.debug("sentence = {}" , s));
            // 保存到知识库中
            vectorDatasetService.insertDatasetKnowledge(datasetId, sentenceList , fileName , fileType) ;
        }
    }

    @Override
    public void saveBatchToDataset(Long datasetId, List<String> sentenceList , String fileName) {

        // 查询datasetId是否存在数据库
        VectorDatasetEntity entity = vectorDatasetService.getById(datasetId) ;
        Assert.isTrue(entity != null , "datasetId is not exists");

        // 处理文本长度问题
        List<String> newSentenceList = new ArrayList<>() ;
        sentenceList.forEach(s -> {
            List<String> list = documentParserService.documentParser(s , maxSegmentLength) ;
            newSentenceList.addAll(list) ;
        }) ;

        String fileType = "txt" ; // 文本格式
        vectorDatasetService.insertDatasetKnowledge(datasetId, newSentenceList , fileName , fileType) ;
    }

    @Override
    public void saveDatasetTmpFile(Long datasetId, String fileName, File targetFile, String fileType, String fileSuffix, PermissionQuery query) {

        DatasetKnowledgeEntity e = new DatasetKnowledgeEntity() ;
        CopyOptions copier = CopyOptions.create().ignoreNullValue();
        BeanUtil.copyProperties(query , e ,copier);

        e.setDatasetId(datasetId);
        e.setDocumentName(fileName);
        e.setFilePath(targetFile.getAbsolutePath());
        e.setHasUploaded(DocumentStatusEnums.UPLOADED.getCode());

        long fileSizeInBytes = targetFile.length();
        double fileSizeInKB = (double) fileSizeInBytes / 1024;
        e.setFileSize(String.valueOf(fileSizeInKB));
        e.setFileType(fileType);
        e.setStatus(DocumentStatusEnums.IMPORT_NOT_COMPLETED.getCode());

        save(e) ;

        // 查询返回当前数据集下未处理的数据

    }

    @Override
    public List<DatasetKnowledgeEntity> queryTmpFileByDatasetId(Long datasetId) {

        LambdaQueryWrapper<DatasetKnowledgeEntity> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(DatasetKnowledgeEntity::getDatasetId , datasetId)
                .eq(DatasetKnowledgeEntity::getHasUploaded, DocumentStatusEnums.UPLOADED.getCode())
                .eq(DatasetKnowledgeEntity::getStatus , DocumentStatusEnums.IMPORT_NOT_COMPLETED.getCode());

        return list(lambdaQuery) ;
    }

    // 此方法不需要开启事务
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void dataUploadToVectorDataset(DataProcessingDto dto) {

        // 更新文件信息
        List<DatasetKnowledgeEntity> list = queryTmpFileByDatasetId(dto.getDatasetId()) ;
        for (DatasetKnowledgeEntity entity : list) {
            entity.setIdealChunk(dto.getIdealChunkLength());
            entity.setProcessingMethod(dto.getProcessingMethod());
            entity.setCustomSeparator(dto.getCustomSplitSymbol());
        }

        Long datasetId = dto.getDatasetId() ;

        // 解析文件信息并上传到dataset
        List<String> sentenceList = new ArrayList<>();

        for(DatasetKnowledgeEntity entity : list){

            String fileSuffix =  Objects.requireNonNull(entity.getFilePath()).substring(entity.getFilePath().lastIndexOf(".")+1);
            File targetFile = new File(entity.getFilePath()) ;
            String documentName = entity.getDocumentName() ;
            String documentContent = entity.getDocumentContent();

            entity.setStatus(DocumentStatusEnums.IMPORT_IN_PROGRESS.getCode()); // 设置为处理中
            updateById(entity);

            try{
                FileTypeEnums constants = FileTypeEnums.getByValue(fileSuffix.toLowerCase()) ;
                assert constants != null;

                sentenceList = switch (constants) {
                    case PDF ->  documentParserService.parsePDF(targetFile);
                    case MD -> documentParserService.parseMD(targetFile);
                    case HTML -> documentParserService.parseHTML(targetFile);
                    case TXT -> documentParserService.parseTxt(documentContent);
                    case EXCEL, EXCEL_OLD -> documentParserService.parseExcel(targetFile);
                    case DOCX -> documentParserService.getContentDocx(targetFile);
                    case DOC -> documentParserService.getContentDoc(targetFile);
                    default -> sentenceList;
                };

                log.debug("sentenceList = {}" , new Gson().toJson(sentenceList));

                sentenceList = documentParserService.documentParser(
                        sentenceList.get(0) ,
                        entity.getIdealChunk() == null ? maxSegmentLength : Integer.parseInt(entity.getIdealChunk())) ;

                List<Long> vectorDataIds = new ArrayList<>();
                if(!sentenceList.isEmpty()){
                    sentenceList.forEach(s -> log.debug("sentence = {}" , s));
                    // 保存到知识库中
                    vectorDataIds = vectorDatasetService.insertDatasetKnowledge(datasetId, sentenceList , documentName , entity.getFileType()) ;
                }

                entity.setUpdateTime(new Date());
                entity.setDocumentCount(sentenceList.size());
                entity.setStatus(DocumentStatusEnums.IMPORT_COMPLETED.getCode());

                // 如果vectorDataIds不为空，则以逗号为分割拼接成字符串
                if (!vectorDataIds.isEmpty()) {
                    entity.setVectorDataIds(vectorDataIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
                }

                updateById(entity);
            }catch(Exception e){

                log.error("数据集导入失败：{}" , e.getMessage());

                entity.setUpdateTime(new Date());
                entity.setStatus(DocumentStatusEnums.IMPORT_FAILED.getCode());
                updateById(entity);
            }
        }

    }

    @Override
    public TableDataInfo queryDocumentPage(DatasetKnowledgeEntity entity , int pageNum, int pageSize) {

        String documentName = entity.getDocumentName() ;
        Long datasetId = entity.getDatasetId() ;
        long total = 0 ;

        List<DocumentVectorBean> list = vectorDatasetService.queryPageByDatasetIdAndDocumentName(
                datasetId,
                documentName ,
                pageNum,
                pageSize,
                total) ;

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(total);
        tableDataInfo.setRows(list);
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("查询成功");

        return tableDataInfo ;
    }

    @Override
    public void updateSegmentContent(SegmentUpdateDto dto) {
        vectorDatasetService.updateSegmentContent(dto);
    }

    @Override
    public void deleteDocument(Long documentId) {
        DatasetKnowledgeEntity entity = getById(documentId) ;

        Long datasetId = entity.getDatasetId() ;
        String documentName = entity.getDocumentName() ;

        vectorDatasetService.deleteDocument(datasetId, documentName);  // 删除向量数据数据
        removeById(documentId);  // 删除数据

        // 数据集更新文件大小
        VectorDatasetEntity vectorDatasetEntity = vectorDatasetService.getById(datasetId) ;
        vectorDatasetEntity.setDatasetSize(vectorDatasetEntity.getDatasetSize() - entity.getDocumentCount());
        vectorDatasetService.update(vectorDatasetEntity) ;
    }
}
