package com.alinesno.infra.base.search.service;


import com.alinesno.infra.base.search.api.SearchUpdateConfigDto;
import com.alinesno.infra.base.search.entity.VectorDatasetEntity;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;

import java.util.List;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @since 2023-09-30
 */
public interface IVectorDatasetService extends IBaseService<VectorDatasetEntity> {

    /**
     * 保存到知识库中
     *
     * @param datasetId
     * @param sentenceList
     * @param fileName
     * @param fileType
     * @return
     */
    List<Long> insertDatasetKnowledge(Long datasetId, List<String> sentenceList, String fileName, String fileType);

    /**
     * 向量搜索
     *
     * @param dto
     * @return
     */
    List<DocumentVectorBean> search(VectorSearchDto dto);

    /**
     * 获取向量引擎
     * @return
     */
    String getVectorEngine();

    /**
     * 重新排序搜索
     * @param dto
     * @return
     */
    List<DocumentVectorBean> rerankSearch(VectorSearchDto dto);

    /**
     * 更新数据集配置
     * @param configDto
     */
    void updateDatasetConfig(SearchUpdateConfigDto configDto);

    /**
     * 最新的数据集
     *
     * @param i
     * @param query
     * @return
     */
    List<VectorDatasetEntity> latestDatasets(int i, PermissionQuery query);

    /**
     * 多数据集搜索
     *
     * @param dto
     * @param datasetIdArr
     * @return
     */
    List<DocumentVectorBean> searchMultiDataset(VectorSearchDto dto, List<Long> datasetIdArr);

    /**
     * 工具选择
     * @param page
     * @param query
     * @return
     */
    TableDataInfo toolSelection(DatatablesPageBean page, PermissionQuery query);

    /**
     * 查询分页
     * @param datasetId
     * @param documentName
     * @param pageNum
     * @param pageSize
     * @param total
     * @return
     */
    List<DocumentVectorBean> queryPageByDatasetIdAndDocumentName(Long datasetId, String documentName, int pageNum, int pageSize, long total);
}