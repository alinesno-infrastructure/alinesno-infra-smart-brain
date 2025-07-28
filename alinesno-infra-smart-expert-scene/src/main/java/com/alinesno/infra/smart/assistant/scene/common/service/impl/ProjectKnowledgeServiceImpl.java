package com.alinesno.infra.smart.assistant.scene.common.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.document.splitter.SimpleTokenizeSplitter;
import com.agentsflex.store.pgvector.PgvectorVectorStore;
import com.agentsflex.store.pgvector.PgvectorVectorStoreConfig;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alinesno.infra.base.search.vector.utils.DashScopeEmbeddingUtils;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.web.adapter.utils.MD5Util;
import com.alinesno.infra.smart.assistant.scene.common.mapper.ProjectKnowledgeMapper;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeGroupService;
import com.alinesno.infra.smart.assistant.scene.common.service.IProjectKnowledgeService;
import com.alinesno.infra.smart.scene.constants.SceneConstants;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeEntity;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProjectKnowledgeServiceImpl extends IBaseServiceImpl<ProjectKnowledgeEntity, ProjectKnowledgeMapper> implements IProjectKnowledgeService {

    @Autowired
    private DashScopeEmbeddingUtils dashScopeEmbeddingUtils;

    @Value("${spring.datasource.dynamic.datasource.postgresql.url:jdbc:postgresql://127.0.0.1:5432/postgres}")
    private String pgVectorUrl ;

    private String pgVectorHost;
    private int pgVectorPort;
    private String pgVectorDatabase ;

    @Value("${spring.datasource.dynamic.datasource.postgresql.username:test}")
    private String pgVectorUsername;

    @Value("${spring.datasource.dynamic.datasource.postgresql.password:123456}")
    private String pgVectorPassword;


    @PostConstruct
    public void init() {
        parsePgVectorUrl();
    }

    /**
     * 解析PostgreSQL JDBC URL获取host和port
     */
    private void parsePgVectorUrl() {
        try {
            // 格式: jdbc:postgresql://host:port/database
            String[] parts = pgVectorUrl.split("//")[1].split(":");
            this.pgVectorHost = parts[0];

            // 获取数据库名称
            this.pgVectorDatabase = parts[1].split("/")[1];

            String[] portAndDb = parts[1].split("/");
            this.pgVectorPort = Integer.parseInt(portAndDb[0]);

            log.info("解析PostgreSQL连接信息 - Host: {}, Port: {}", pgVectorHost, pgVectorPort);
        } catch (Exception e) {
            log.error("解析PostgreSQL URL失败，使用默认值", e);
            this.pgVectorHost = "127.0.0.1";
            this.pgVectorPort = 5432;
        }
    }

    /**
     * 导入数据并进行向量化存储
     * @param entity 项目知识实体
     */
    @Override
    public void importData(ProjectKnowledgeEntity entity) {
        if (entity == null || entity.getDocContent() == null) {
            log.error("导入数据失败: 实体或内容为空");
            throw new IllegalArgumentException("实体或内容不能为空");
        }

        String fileName = entity.getDocName();
        String content = entity.getDocContent();

        String md5 = MD5Util.encode(fileName) ;
        entity.setDocMd5(md5);

        save(entity);

        Long id = entity.getId();
        Long groupId = entity.getGroupId();

        IProjectKnowledgeGroupService projectKnowledgeGroupService = SpringUtil.getBean(IProjectKnowledgeGroupService.class);

        try {
            // 获取知识分组信息
            ProjectKnowledgeGroupEntity projectKnowledgeGroup = projectKnowledgeGroupService.getById(groupId);
            if (projectKnowledgeGroup == null) {
                log.error("导入数据失败: 未找到对应的知识分组, groupId={}", groupId);
                throw new IllegalArgumentException("未找到对应的知识分组");
            }

            String collectionIndexName = projectKnowledgeGroup.getVectorDatasetName() ;

            // 文件分块处理
            List<Document> chunks = splitDocument(content);
            log.info("文件分块完成, 共分成 {} 个块", chunks.size());

            // 向量化处理
            List<Document> vectorizedDocs = processVectorization(chunks, fileName, id, collectionIndexName);
            log.info("向量化处理完成, 共生成 {} 个向量", vectorizedDocs.size());

            // 存储向量
            storeVectors(vectorizedDocs, collectionIndexName);
            log.info("向量存储完成, 集合名称: {}", collectionIndexName);

            // 更新实体状态
            entity.setVectorStatus(1); // 标记为已向量化
            this.update(entity);

        } catch (Exception e) {
            log.error("导入数据过程中发生异常", e);
            throw new RuntimeException("导入数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 文档分块处理
     * @param content 文档内容
     * @return 分块后的文档列表
     */
    private List<Document> splitDocument(String content) {
        SimpleTokenizeSplitter splitter = new SimpleTokenizeSplitter(512); // 512 tokens 为一块
        return splitter.split(Document.of(content));
    }

    /**
     * 向量化处理
     * @param chunks 文档分块
     * @param fileName 文件名
     * @param docId 文档ID
     * @param collectionIndexName 集合名称
     * @return 向量化后的文档列表
     */
    private List<Document> processVectorization(List<Document> chunks, String fileName, Long docId, String collectionIndexName) throws NoApiKeyException, InputRequiredException {

        if (chunks == null || chunks.isEmpty()) {
            log.warn("文档分块列表为空，无法进行向量化处理");
            return new ArrayList<>();
        }

        List<Document> vectorizedDocs = new ArrayList<>();
        int batchSize = 10; // 每批处理的最大文本数量

        // 计算需要处理的批次数量
        int totalBatches = (chunks.size() + batchSize - 1) / batchSize;

        for (int batch = 0; batch < totalBatches; batch++) {
            // 计算当前批次的起始和结束索引
            int startIndex = batch * batchSize;
            int endIndex = Math.min(startIndex + batchSize, chunks.size());

            // 提取当前批次的文本
            List<Document> batchChunks = chunks.subList(startIndex, endIndex);
            List<String> textList = new ArrayList<>();

            for (Document chunk : batchChunks) {
                textList.add(chunk.getContent());
            }

            log.info("处理第 {} 批向量化，共 {} 条文本", batch + 1, textList.size());

            // 批量获取向量
            List<TextEmbeddingResultItem> embeddingResultItems = dashScopeEmbeddingUtils.embedSingleList(textList);

            // 处理当前批次的向量化结果
            for (int i = 0; i < batchChunks.size(); i++) {
                Document chunk = batchChunks.get(i);
                TextEmbeddingResultItem embeddingResultItem = embeddingResultItems.get(i);

                // 转换为double数组
                double[] vector = embeddingResultItem.getEmbedding().stream()
                        .mapToDouble(Double::doubleValue)
                        .toArray();

                // 构建元数据
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("fileName", fileName);
                metadata.put("docId", docId);
                metadata.put("collectionIndexName", collectionIndexName);
                metadata.put("chunkIndex", startIndex + i); // 保持全局索引
                metadata.put("totalChunks", chunks.size());

                // 创建带向量的文档
                Document vectorizedDoc = new Document(chunk.getContent());
                vectorizedDoc.setVector(vector);
                vectorizedDoc.setMetadataMap(metadata);

                vectorizedDocs.add(vectorizedDoc);
            }
        }

        log.info("向量化处理完成，共生成 {} 个向量", vectorizedDocs.size());
        return vectorizedDocs;
    }

    /**
     * 存储向量到PGVector
     * @param documents 向量化文档列表
     * @param collectionIndexName 集合名称
     */
    private void storeVectors(List<Document> documents, String collectionIndexName) {
        PgvectorVectorStore pgvectorVectorStore =  getPgvectorVectorStore(collectionIndexName);

        try {
            // 分批存储，每批100个
            int batchSize = 100;
            for (int i = 0; i < documents.size(); i += batchSize) {
                int end = Math.min(i + batchSize, documents.size());
                List<Document> batch = documents.subList(i, end);
                pgvectorVectorStore.store(batch);
                log.debug("已存储向量 {} 到 {}", end, collectionIndexName);
            }
        } catch (Exception e) {
            log.error("存储向量到PGVector失败", e);
            throw new RuntimeException("存储向量失败: " + e.getMessage(), e);
        }
    }

    @NotNull
    private PgvectorVectorStore getPgvectorVectorStore(String collectionIndexName) {
        PgvectorVectorStoreConfig config = new PgvectorVectorStoreConfig();

        config.setHost(pgVectorHost);
        config.setPort(pgVectorPort);
        config.setDatabaseName(pgVectorDatabase);
        config.setUsername(pgVectorUsername);
        config.setPassword(pgVectorPassword);

        config.setVectorDimension(1024);
        config.setUseHnswIndex(true);
        config.setAutoCreateCollection(true);
        config.setDefaultCollectionName(SceneConstants.ALINESNO_SCENE_PROJECT_VECTOR_DOCUMENT_INDEX_NAME) ;
        config.setIndexName(collectionIndexName);

        return new PgvectorVectorStore(config);
    }
}