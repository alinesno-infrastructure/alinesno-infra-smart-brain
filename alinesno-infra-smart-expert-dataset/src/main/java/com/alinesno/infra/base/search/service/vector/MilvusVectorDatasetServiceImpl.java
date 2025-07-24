package com.alinesno.infra.base.search.service.vector;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.search.api.SearchUpdateConfigDto;
import com.alinesno.infra.base.search.entity.VectorDatasetEntity;
import com.alinesno.infra.base.search.mapper.VectorDatasetMapper;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.base.search.vector.dto.EmbeddingBean;
import com.alinesno.infra.base.search.vector.service.IMilvusDataService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @since 2023-09-30
 */
@Slf4j
@Service
public class MilvusVectorDatasetServiceImpl extends IBaseServiceImpl<VectorDatasetEntity, VectorDatasetMapper> implements IVectorDatasetService {

    @Autowired
    private IMilvusDataService milvusDataService;

    private static final Gson gson = new Gson() ;

    @Async
    @Override
    public List<Long> insertDatasetKnowledge(Long datasetId, List<String> sentenceList, String fileName, String fileType) {

        VectorDatasetEntity vectorDatasetEntity = getById(datasetId) ;

        int datasetSize = sentenceList.size() + vectorDatasetEntity.getDatasetSize() ;
        log.debug("datasetSize = {}" , datasetSize);

        vectorDatasetEntity.setDatasetSize(datasetSize);
        update(vectorDatasetEntity) ;

        String collectionName = vectorDatasetEntity.getCollectionName();

        for(String content : sentenceList){

            String vectorData = null ; //  embeddingConsumer.embeddings(gson.toJson(List.of(new EmbeddingText(content)))) ;
            log.debug("vectorData = {}" , vectorData);

            EmbeddingBean embeddingBean = new EmbeddingBean() ;

            embeddingBean.setId(IdUtil.getSnowflakeNextId());
            embeddingBean.setDatasetId(datasetId);
            embeddingBean.setDocumentContent(content);
            embeddingBean.setDocumentVector(vectorData);

            milvusDataService.insertData(collectionName , "novel" , embeddingBean);
        }

        milvusDataService.buildIndexByCollection(collectionName) ;

        return Collections.emptyList();
    }

    @Override
    public List<DocumentVectorBean> search(VectorSearchDto dto) {
        return null;
    }

    @Override
    public String getVectorEngine() {
        return "milvus" ;
    }

    @Override
    public List<DocumentVectorBean> rerankSearch(VectorSearchDto dto) {
        return null;
    }

    @Override
    public void updateDatasetConfig(SearchUpdateConfigDto configDto) {

    }

    @Override
    public List<VectorDatasetEntity> latestDatasets(int i, PermissionQuery query) {
        return null;
    }

    @Override
    public List<DocumentVectorBean> searchMultiDataset(VectorSearchDto dto, List<Long> datasetIdArr) {
        return null;
    }

    @Override
    public TableDataInfo toolSelection(DatatablesPageBean page, PermissionQuery query) {
        return null;
    }

    @Override
    public List<DocumentVectorBean> queryPageByDatasetIdAndDocumentName(Long datasetId, String documentName, int pageNum, int pageSize, long total) {
        return null;
    }

}