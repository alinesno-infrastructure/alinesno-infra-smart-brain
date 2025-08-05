package com.alinesno.infra.base.search.service;

import com.alinesno.infra.base.search.api.DataProcessingDto;
import com.alinesno.infra.base.search.api.SegmentUpdateDto;
import com.alinesno.infra.base.search.entity.DatasetKnowledgeEntity;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.services.IBaseService;

import java.io.File;
import java.util.List;

/**
 * 数据集知识管理Service接口
 * 该接口提供了对数据集知识的基本操作，包括文档解析等功能
 * 主要用于处理数据集中的文档，提取并保存有用信息
 *
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IDatasetKnowledgeService extends IBaseService<DatasetKnowledgeEntity> {

    /**
     * 文档解析功能
     * 该方法用于接收一个数据集ID和一组句子，将这些句子作为文档内容进行解析
     * 并根据文件名和文件类型对解析结果进行相应处理
     *
     * @param datasetId 数据集的唯一标识
     * @param sentenceList 待解析的句子列表，代表文档的内容
     * @param fileName 文件名，用于在解析过程中标识文档
     * @param fileType 文件类型，影响解析方式或存储格式
     */
    void parserDocument(Long datasetId, List<String> sentenceList, String fileName, String fileType);

    /**
     * 批量保存到数据集
     * @param datasetId
     * @param sentenceList
     */
    void saveBatchToDataset(Long datasetId, List<String> sentenceList , String fileName);

    /**
     * 保存数据集临时文件
     *
     * @param datasetId
     * @param fileName
     * @param targetFile
     * @param fileType
     * @param fileSuffix
     * @param query
     * @return
     */
    void saveDatasetTmpFile(Long datasetId, String fileName, File targetFile, String fileType, String fileSuffix, PermissionQuery query);

    /**
     * 查询临时文件
     * @param datasetId
     * @return
     */
    List<DatasetKnowledgeEntity> queryTmpFileByDatasetId(Long datasetId);

    /**
     * 处理解析并上传到向量库
     * @param dto
     * @return
     */
    void dataUploadToVectorDataset(DataProcessingDto dto);

    /**
     * 查询文档分页向量存储的数据情况
     * @param entity
     * @param pageNum
     * @param pageSize
     * @return
     */
    TableDataInfo queryDocumentPage(DatasetKnowledgeEntity entity , int pageNum, int pageSize);

    /**
     * 更新向量存储的文档内容
     * @param dto
     */
    void updateSegmentContent(SegmentUpdateDto dto);

    /**
     * 删除知识库文档
     * @param documentId
     */
    void deleteDocument(Long documentId);
}
