package com.alinesno.infra.base.search.service.vector;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.search.api.SearchUpdateConfigDto;
import com.alinesno.infra.base.search.entity.VectorDatasetEntity;
import com.alinesno.infra.base.search.enums.SearchType;
import com.alinesno.infra.base.search.mapper.VectorDatasetMapper;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.base.search.vector.service.IPgVectorService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.smart.assistant.adapter.RerankConsumer;
import com.alinesno.infra.smart.assistant.adapter.dto.*;
import com.alinesno.infra.smart.assistant.enums.ModelDataScopeOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @since 2023-09-30
 */
@Slf4j
@Primary
@Service
public class PgVectorDatasetServiceImpl extends IBaseServiceImpl<VectorDatasetEntity, VectorDatasetMapper> implements IVectorDatasetService {

    @Autowired
    private IPgVectorService pgVectorService;

    @Autowired
    private RerankConsumer rerankConsumer;

    @Value("${alinesno.base.search.dashscope.api-key:}")
    private String apiKey;

    @Value("${alinesno.base.search.dashscope.rerank-model-name:gte-rerank}")
    private String modelName ;

    private static final int MAX_RESULTS = 5; // 最大结果数量
    private static final Gson gson = new Gson() ;

    @Override
    public List<Long> insertDatasetKnowledge(Long datasetId, List<String> sentenceList, String fileName, String fileType) {

        VectorDatasetEntity vectorDatasetEntity = getById(datasetId) ;

        int datasetSize = sentenceList.size() + vectorDatasetEntity.getDatasetSize() ;
        log.debug("datasetSize = {}" , datasetSize);

        vectorDatasetEntity.setDatasetSize(datasetSize);
        update(vectorDatasetEntity) ;

        String collectionName = vectorDatasetEntity.getCollectionName();

        List<Long> ids = new ArrayList<>();

        int count = 1 ;
        for(String content : sentenceList){

            DocumentVectorBean embeddingBean = new DocumentVectorBean() ;

            long id = IdUtil.getSnowflakeNextId();
            embeddingBean.setId(id) ;
            embeddingBean.setIndexName(collectionName);
            embeddingBean.setDataset_id(datasetId);
            embeddingBean.setDocument_title(fileName);
            embeddingBean.setDocument_content(content);
            embeddingBean.setPage(count) ;
            embeddingBean.setSourceType(fileType);
            ids.add(id) ;

            count ++ ;

            pgVectorService.insertVector(embeddingBean);
        }

        return ids ;
    }

    @Override
    public List<DocumentVectorBean> search(VectorSearchDto dto) {

        List<DocumentVectorBean> list = null;

        VectorDatasetEntity vectorDatasetEntity = getById(dto.getDatasetId()) ;

        if(vectorDatasetEntity.getSearchType() == null){
            vectorDatasetEntity.setSearchType(SearchType.VECTOR.getCode());
        }

        if(vectorDatasetEntity.getSearchType().equals(SearchType.VECTOR.getCode()) && StringUtils.hasLength(dto.getMinRelevance()) && dto.getQuoteLimit() != 0){  // 最新的搜索方式
            dto.setMinRelevance(vectorDatasetEntity.getMinRelevance() == null ? dto.getMinRelevance() : vectorDatasetEntity.getMinRelevance()) ;
            dto.setQuoteLimit(vectorDatasetEntity.getQuoteLimit() == 0 ? dto.getQuoteLimit(): vectorDatasetEntity.getQuoteLimit());

            if(vectorDatasetEntity.getSearchType().equals(SearchType.VECTOR.getCode())){  // 向量检索
                list =  pgVectorService.queryVectorDocument(dto) ;
            } else if( vectorDatasetEntity.getSearchType().equals(SearchType.HYBRID.getCode())) {  // 混合检索
                list =  pgVectorService.queryHybridDocument(dto) ;
            }
        }else if(vectorDatasetEntity.getSearchType().equals(SearchType.FULLTEXT.getCode())){  // 全文检索
            list =  pgVectorService.queryFullTextDocument(dto) ;
        } else{
             list =  pgVectorService.queryVectorDocument(dto.getCollectionName() , dto.getSearchText() , dto.getTopK()) ;
        }

        // TODO 根据分数重新排序,调整在RRF重新排序
        Assert.isTrue(list != null , "搜索结果为空");
        return list.stream().sorted((o1, o2) -> Float.compare(o2.getScore(), o1.getScore())).collect(Collectors.toList());
    }

    @Override
    public String getVectorEngine() {
        return "pgvector" ;
    }

    /**
     * 重新排序搜索结果
     * 此方法通过对初步搜索结果进行深度分析，重新排序文档，以提供更相关的文档在前
     *
     * @param dto 包含搜索参数的数据传输对象
     * @return 重新排序后的文档列表，按相关性排序
     */
    @Override
    public List<DocumentVectorBean> rerankSearch(VectorSearchDto dto) {
        // 初始搜索，获取初步的搜索结果
        List<DocumentVectorBean> topksList = search(dto);

        // 获取查询文档，如果搜索结果为空，则为null
        DocumentVectorBean queryDocument = topksList.isEmpty() ? null : topksList.get(0);

        // 将搜索结果的文档内容转换为字符串列表
        List<String> arrayDocument = topksList.stream()
                    .map(DocumentVectorBean::getDocument_content)
                    .toList();

        // 记录调试信息，关于搜索结果的数量
        log.debug("topsList size = {}", topksList.size());

        // 准备重新排序请求
        TextRerankRequest request = new TextRerankRequest();
        request.setModel(modelName);
        request.setQuery(dto.getSearchText());
        request.setDocuments(arrayDocument);
        request.setReturn_documents(true);
        request.setTop_n(MAX_RESULTS);

//        request.setInput(new TextRerankRequest.Input());
//        request.getInput().setQuery(dto.getSearchText());
//        request.getInput().setDocuments(arrayDocument);
//        request.setParameters(new TextRerankRequest.Parameters());
//        request.getParameters().setReturn_documents(true);
//        request.getParameters().setTop_n(MAX_RESULTS);

        // 发起重新排序请求并处理响应
        try {
            String output = rerankConsumer.rerank(request, "Bearer " + apiKey);
            RerankOutput rerankOutput = gson.fromJson(output, RerankOutput.class);

            // 记录调试信息，关于输出内容和重新排序结果的数量
            log.debug("output = {}", "[REDACTED]");
            log.debug("rerankOutput size = {}", rerankOutput.getOutput().getResults().size());

            // 构建重新排序后的文档列表
            List<DocumentVectorBean> rerankTopksList = new ArrayList<>();
            int pageCount = 1;
            for (Result result : rerankOutput.getOutput().getResults()) {
                if (pageCount > MAX_RESULTS) break;
                DocumentVectorBean bean = createRerankedDocumentBean(queryDocument, result, pageCount++);
                rerankTopksList.add(bean);
            }

            // 返回按相关性降序排列的重新排序后的文档列表
            return rerankTopksList.stream()
                    .sorted(Comparator.comparing(DocumentVectorBean::getScore).reversed())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 捕获并记录重新排序过程中的异常
            log.error("Error during reranking: ", e);
            throw e;
        }
    }

    @Override
    public void updateDatasetConfig(SearchUpdateConfigDto configDto) {
        VectorDatasetEntity vectorDatasetEntity = getById(configDto.getDatasetId()) ;

        vectorDatasetEntity.setSearchType(configDto.getSearchType());
        vectorDatasetEntity.setQuoteLimit(configDto.getQuoteLimit());
        vectorDatasetEntity.setReorderResults(configDto.getReorderResults()?1:0);
        vectorDatasetEntity.setMinRelevance(configDto.getMinRelevance()); // 转换成整数

        update(vectorDatasetEntity) ;
    }

    @Override
    public List<VectorDatasetEntity> latestDatasets(int top, PermissionQuery query) {

        if(top <= 0){
            top = 10 ;
        }

        LambdaQueryWrapper<VectorDatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(VectorDatasetEntity.class) ;
        query.toWrapper(queryWrapper);
        queryWrapper.orderByDesc(VectorDatasetEntity::getUpdateTime);
        queryWrapper.eq(VectorDatasetEntity::getIsMemory, 0);
        queryWrapper.last("limit " + top) ;

        return list(queryWrapper) ;
    }

    @Override
    public List<DocumentVectorBean> searchMultiDataset(VectorSearchDto dto, List<Long> datasetIdArr) {

        if(datasetIdArr == null || datasetIdArr.isEmpty()){
            return Collections.emptyList();
        }

        List<DocumentVectorBean> list =  pgVectorService.queryMultiVectorDocument(datasetIdArr , dto.getSearchText() , dto.getTopK()) ;

        // Assert.isTrue(list != null , "搜索结果为空");
        return list.stream().sorted((o1, o2) -> Float.compare(o2.getScore(), o1.getScore())).collect(Collectors.toList());
    }

    @Override
    public TableDataInfo toolSelection(DatatablesPageBean page, PermissionQuery query) {
        // TODO: 待处理分页的问题
        int pageNow = page.getPageNum();
        int pageSize = page.getPageSize();

        LambdaQueryWrapper<VectorDatasetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.setEntityClass(VectorDatasetEntity.class) ;
        query.toWrapper(wrapper);

        Page<VectorDatasetEntity> pageResult = new Page<>(pageNow, pageSize) ;
        pageResult = this.page(pageResult, wrapper);

        // 查询出公共的工具
        LambdaQueryWrapper<VectorDatasetEntity> publicWrapper = new LambdaQueryWrapper<>();
        publicWrapper.eq(VectorDatasetEntity::getAccessPermission, ModelDataScopeOptions.PUBLIC.getValue());
        List<VectorDatasetEntity> publicTools = this.list(publicWrapper);

        if(publicTools == null){
            publicTools = new ArrayList<>();
        }

        TableDataInfo tableDataInfo = new TableDataInfo();

        // 存储 pageResult.getRecords() 中的 id
        Set<Long> recordIds = new HashSet<>();
        for (VectorDatasetEntity record : pageResult.getRecords()) {
            recordIds.add(record.getId());
        }

        // 过滤掉 publicTools 中已经存在于 pageResult.getRecords() 的元素
        List<VectorDatasetEntity> uniquePublicTools = new ArrayList<>();
        for (VectorDatasetEntity tool : publicTools) {
            if (!recordIds.contains(tool.getId())) {
                uniquePublicTools.add(tool);
            }
        }

        // 添加不重复的公共工具
        uniquePublicTools.addAll(pageResult.getRecords());

        tableDataInfo.setRows(uniquePublicTools);
        tableDataInfo.setTotal(pageResult.getTotal() + publicTools.size());

        return tableDataInfo;
    }

    /**
     * 创建重新排序后的文档对象
     * 此方法复制查询文档的属性，并根据重新排序的结果更新特定字段
     *
     * @param queryDocument 查询文档，可能为null
     * @param result 重新排序的结果项
     * @param pageCount 当前结果项的页码
     * @return 更新后的文档对象
     */
    private DocumentVectorBean createRerankedDocumentBean(DocumentVectorBean queryDocument, Result result, int pageCount) {
        DocumentVectorBean bean = new DocumentVectorBean();
        // 复制查询文档的属性到新的文档对象
        BeanUtils.copyProperties(queryDocument, bean);
        // 根据重新排序结果更新文档内容和分数
        bean.setDocument_content(result.getDocument().getText());
        bean.setScore((float) result.getRelevance_score());
        bean.setPage(pageCount);
        return bean;
    }

    @Override
    public List<DocumentVectorBean> queryPageByDatasetIdAndDocumentName(Long datasetId, String documentName, int pageNum, int pageSize, long total) {
        // 参数校验
        Assert.notNull(datasetId, "datasetId cannot be null");
        Assert.hasText(documentName, "documentName cannot be empty");

        // 获取数据集信息
        VectorDatasetEntity dataset = getById(datasetId);
        Assert.notNull(dataset, "Dataset not found with id: " + datasetId);

        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 构建查询条件
        Map<String, Object> params = new HashMap<>();
        params.put("datasetId", datasetId);
        params.put("documentName", documentName);
        params.put("limit", pageSize);
        params.put("offset", offset);

        // 执行分页查询
        List<DocumentVectorBean> result = pgVectorService.queryPageByDatasetIdAndDocumentName(params);

        // 如果total为0，则查询总记录数
        if (total == 0 && !result.isEmpty()) {
            total = pgVectorService.countByDatasetIdAndDocumentName(datasetId, documentName);
        }

        return result;
    }
}