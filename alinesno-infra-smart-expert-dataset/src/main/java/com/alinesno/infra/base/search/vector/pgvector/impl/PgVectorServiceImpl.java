package com.alinesno.infra.base.search.vector.pgvector.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.search.vector.service.IPgVectorService;
import com.alinesno.infra.base.search.vector.utils.DashScopeEmbeddingUtils;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.pgvector.PGvector;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Component
public class PgVectorServiceImpl implements IPgVectorService {

    // 变量转换成大写
    private static final String ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME = "alinesno_search_vector_document";

    @Resource(name = "pgvectorJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DashScopeEmbeddingUtils dashScopeEmbeddingUtils;

    // 提取获取嵌入向量的方法
    @SneakyThrows
    @Override
    public PGvector getEmbeddingVector(String queryText) {
        List<Double> embeddingVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(queryText);
        return new PGvector(embeddingVector);
    }

    // 提取将查询结果转换为 DocumentVectorBean 对象列表的方法
    private List<DocumentVectorBean> convertToDocumentVectorBeans(List<Map<String, Object>> rows, int quoteLimit) {
        List<DocumentVectorBean> results = new ArrayList<>();
        int totalContentLength = 0;

        for (Map<String, Object> row : rows) {
            String documentContent = (String) row.get("document_content");
            if (documentContent == null) {
                documentContent = "";
            }
            int currentLength = documentContent.length();

            if (quoteLimit > 0 && totalContentLength + currentLength > quoteLimit) {
                break;
            }

            DocumentVectorBean documentVectorBean = new DocumentVectorBean();
            documentVectorBean.setId((Long) row.get("id"));
            documentVectorBean.setDataset_id((Long) row.get("dataset_id"));
            documentVectorBean.setIndexName((String) row.get("index_name"));
            documentVectorBean.setDocument_title((String) row.get("document_title"));
            documentVectorBean.setDocument_desc((String) row.get("document_desc"));
            documentVectorBean.setDocument_content((String) row.get("document_content"));
            if (row.containsKey("cosine_similarity")) {
                documentVectorBean.setScore(((Number) row.get("cosine_similarity")).floatValue());
            } else {
                documentVectorBean.setScore(0f);
            }
            documentVectorBean.setPage(((Number) row.get("page")).intValue());
            documentVectorBean.setDoc_chunk((String) row.get("doc_chunk"));
            documentVectorBean.setToken_size(((Number) row.get("token_size")).intValue());
            documentVectorBean.setSourceFile((String) row.get("source_file"));
            documentVectorBean.setSourceUrl((String) row.get("source_url"));
            documentVectorBean.setSourceType((String) row.get("source_type"));
            documentVectorBean.setAuthor((String) row.get("author"));

            results.add(documentVectorBean);
            totalContentLength += currentLength;
        }

        return results;
    }

    @SneakyThrows
    @Override
    public List<DocumentVectorBean> queryDocument(String indexName, String fileName, String queryText, int size) {
        PGvector embeddingVector = getEmbeddingVector(queryText);
        String likeCondition = "%" + queryText + "%";
        Object[] queryParams = new Object[]{embeddingVector, indexName, fileName, likeCondition, likeCondition, size};

        String querySql = "SELECT *, 1 - (document_embedding <=> ?) AS cosine_similarity " +
                "FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME +
                " WHERE index_name = ? " +
                " AND source_file = ? " +
                " AND (document_title ILIKE ? OR document_content ILIKE ?) " +
                " ORDER BY cosine_similarity DESC LIMIT ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(querySql, queryParams);
        return convertToDocumentVectorBeans(rows, 0);
    }

    @SneakyThrows
    @Override
    public List<DocumentVectorBean> queryVectorDocument(String indexName, String queryText, int size) {
        PGvector embeddingVector = getEmbeddingVector(queryText);
        Object[] neighborParams = {embeddingVector, indexName, size};

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * , 1 - (document_embedding <=> ?) AS cosine_similarity " +
                        "FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " " +
                        "WHERE index_name = ? " +
                        "ORDER BY cosine_similarity DESC LIMIT ?", neighborParams);

        return convertToDocumentVectorBeans(rows, 0);
    }

    @SneakyThrows
    @Override
    public List<DocumentVectorBean> queryVectorDocument(VectorSearchDto dto) {
        String indexName = dto.getCollectionName();
        String queryText = dto.getSearchText();
        int size = dto.getTopK();
        double minRelevance = Double.parseDouble(dto.getMinRelevance());
        int quoteLimit = dto.getQuoteLimit();

        PGvector embeddingVector = getEmbeddingVector(queryText);
        Object[] neighborParams = {embeddingVector, indexName, minRelevance, size};

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "WITH similarity_cte AS (" +
                        "SELECT *, 1 - (document_embedding <=> ?) AS cosine_similarity " +
                        "FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " " +
                        "WHERE index_name = ?" +
                        ") " +
                        "SELECT * FROM similarity_cte " +
                        "WHERE cosine_similarity >= ? " +
                        "ORDER BY cosine_similarity DESC LIMIT ?", neighborParams);

        return convertToDocumentVectorBeans(rows, quoteLimit);
    }

//    @Override
//    public void createVectorIndex(String indexName) {
//        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
//        String ddl = "CREATE TABLE IF NOT EXISTS " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " (\n" +
//                "    id BIGSERIAL PRIMARY KEY,\n" +
//                "    dataset_id BIGINT NOT NULL,\n" +
//                "    index_name VARCHAR(255) NOT NULL,\n" +
//                "    document_title TEXT,\n" +
//                "    document_desc text,  \n" +
//                "    document_content TEXT,\n" +
//                "    document_embedding VECTOR (1024),\n" +
//                "    token_size int,  \n" +
//                "    doc_chunk text,  \n" +
//                "    score REAL,\n" +
//                "    page INTEGER,\n" +
//                "    source_file VARCHAR(255),\n" +
//                "    source_url TEXT,\n" +
//                "    source_type VARCHAR(50),\n" +
//                "    author VARCHAR(255),\n" +
//                "    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP\n" +
//                ");";
//
//        jdbcTemplate.execute(ddl);
//        jdbcTemplate.execute("CREATE INDEX ON " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " USING ivfflat (document_embedding vector_cosine_ops) WITH (lists = 100);");
//        jdbcTemplate.execute("CREATE INDEX idx_fts_" + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " ON " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " USING gin(to_tsvector('english', document_title || ' ' || document_content));");
//    }

    @Override
    public void createVectorIndex(String indexName) {
        // 1. 启用pgvector扩展
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");

        // 2. 创建向量文档存储表
        String ddl = "CREATE TABLE IF NOT EXISTS " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " (\n" +
                "    id BIGSERIAL PRIMARY KEY,\n" +
                "    dataset_id BIGINT NOT NULL,\n" +
                "    index_name VARCHAR(255) NOT NULL,\n" +
                "    document_title TEXT,\n" +
                "    document_desc TEXT,\n" +
                "    document_content TEXT,\n" +
                "    document_embedding VECTOR(1024),\n" +
                "    token_size INT,\n" +
                "    doc_chunk TEXT,\n" +
                "    score REAL,\n" +
                "    page INTEGER,\n" +
                "    source_file VARCHAR(255),\n" +
                "    source_url TEXT,\n" +
                "    source_type VARCHAR(50),\n" +
                "    author VARCHAR(255),\n" +
                "    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP\n" +
                ")";

        jdbcTemplate.execute(ddl);
        log.debug("创建向量文档表成功");

        // 3. 创建向量索引（添加IF NOT EXISTS条件）
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vector_embedding ON " +
                ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME +
                " USING ivfflat (document_embedding vector_cosine_ops) WITH (lists = 100)");

        // 4. 创建全文检索索引（添加IF NOT EXISTS条件）
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_fts_content ON " +
                ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME +
                " USING gin(to_tsvector('english', document_title || ' ' || document_content))");

        // 5. 添加表和字段的中文注释
        jdbcTemplate.execute("COMMENT ON TABLE " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME +
                " IS '向量文档存储表，用于存储文档内容及其向量嵌入表示'");

        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".id IS '自增主键ID'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".dataset_id IS '数据集ID，支持多租户场景'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".index_name IS '索引/集合名称，用于文档分组'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".document_title IS '文档标题'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".document_desc IS '文档描述'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".document_content IS '文档完整内容'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".document_embedding IS '1024维度的文档嵌入向量（BGE模型输出）'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".token_size IS '文档token数量'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".doc_chunk IS '文档分块内容（如使用分块策略）'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".score IS '相关性评分'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".page IS '原始页码（如PDF文档）'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".source_file IS '来源文件名'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".source_url IS '来源URL链接'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".source_type IS '来源类型（如pdf/html/news等）'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".author IS '文档作者'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + ".created_at IS '记录创建时间'");
    }

    @SneakyThrows
    @Override
    public void insertVector(DocumentVectorBean documentVectorBean) {
        String indexName = documentVectorBean.getIndexName();
        PGvector embeddingVector = getEmbeddingVector(documentVectorBean.getDocument_content());
        Object[] insertParams = getObjects(documentVectorBean, embeddingVector);

        int count = jdbcTemplate.update(
                "INSERT INTO " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " " +
                        "(" +
                        "id, " +
                        "dataset_id, " +
                        "index_name, " +
                        "document_title, " +
                        "document_desc, " +
                        "document_content, " +
                        "document_embedding, " +
                        "token_size, " +
                        "doc_chunk, " +
                        "score, " +
                        "page, " +
                        "source_file, " +
                        "source_url, " +
                        "source_type, " +
                        "author" +
                        ") " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                insertParams
        );
        log.debug("插入了 " + count + " 条记录");
    }

    @NotNull
    private static Object[] getObjects(DocumentVectorBean documentVectorBean, PGvector pgVector) {
        return new Object[]{
                IdUtil.getSnowflakeNextId(),
                documentVectorBean.getDataset_id(),
                documentVectorBean.getIndexName(),
                documentVectorBean.getDocument_title(),
                documentVectorBean.getDocument_desc(),
                documentVectorBean.getDocument_content(),
                pgVector,
                documentVectorBean.getToken_size(),
                documentVectorBean.getDoc_chunk(),
                documentVectorBean.getScore(),
                documentVectorBean.getPage(),
                documentVectorBean.getSourceFile(),
                documentVectorBean.getSourceUrl(),
                documentVectorBean.getSourceType(),
                documentVectorBean.getAuthor()
        };
    }

    @SneakyThrows
    @Override
    public void updateVector(DocumentVectorBean documentVectorBean) {
        String indexName = documentVectorBean.getIndexName();
        PGvector embeddingVector = getEmbeddingVector(documentVectorBean.getDocument_content());
        Object[] updateParams = getObjects(documentVectorBean, embeddingVector);

        int count = jdbcTemplate.update(
                "UPDATE " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " SET " +
                        "dataset_id = ?, " +
                        "index_name = ?, " +
                        "document_title = ?, " +
                        "document_desc = ?, " +
                        "document_content = ?, " +
                        "document_embedding = ?, " +
                        "token_size = ?, " +
                        "doc_chunk = ?, " +
                        "score = ?, " +
                        "page = ?, " +
                        "source_file = ?, " +
                        "source_url = ?, " +
                        "source_type = ?, " +
                        "author = ? " +
                        "WHERE id = ?",
                updateParams[1], // dataset_id
                updateParams[2], // index_name
                updateParams[3], // document_title
                updateParams[4], // document_desc
                updateParams[5], // document_content
                updateParams[6], // document_embedding
                updateParams[7], // token_size
                updateParams[8], // doc_chunk
                updateParams[9], // score
                updateParams[10], // page
                updateParams[11], // source_file
                updateParams[12], // source_url
                updateParams[13], // source_type
                updateParams[14], // author
                documentVectorBean.getId() // id
        );
        log.debug("更新了 " + count + " 条记录");
    }

    @Override
    public void deleteVectorIndex(String indexName, long documentId) {
        int count = jdbcTemplate.update(
                "DELETE FROM " + indexName + " WHERE id = ?",
                documentId
        );
        log.debug("删除了 " + count + " 条记录");
    }

    @Override
    public List<DocumentVectorBean> queryFullTextDocument(VectorSearchDto dto) {
        String indexName = dto.getCollectionName();
        String queryText = dto.getSearchText();
        int size = dto.getTopK();
        int quoteLimit = dto.getQuoteLimit();

        Object[] params = {indexName, queryText, queryText, size};

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT " +
                        "    id, " +
                        "    dataset_id, " +
                        "    index_name, " +
                        "    document_title, " +
                        "    document_desc, " +
                        "    document_content, " +
                        "    token_size, " +
                        "    doc_chunk, " +
                        "    score, " +
                        "    page, " +
                        "    source_file, " +
                        "    source_url, " +
                        "    source_type, " +
                        "    author " +
                        "FROM " +
                        "    alinesno_search_vector_document " +
                        "WHERE " +
                        "    index_name = ? " +
                        "    AND to_tsvector('english', document_title || ' ' || document_content) @@ plainto_tsquery('english', ?) " +
                        "ORDER BY " +
                        "    ts_rank(to_tsvector('english', document_title || ' ' || document_content), plainto_tsquery('english', ?)) DESC " +
                        "LIMIT " +
                        "    ?;",
                params);

        return convertToDocumentVectorBeans(rows, quoteLimit);
    }

    @SneakyThrows
    @Override
    public List<DocumentVectorBean> queryHybridDocument(VectorSearchDto dto) {
        String indexName = dto.getCollectionName();
        String queryText = dto.getSearchText();
        int size = dto.getTopK();
        int quoteLimit = dto.getQuoteLimit();

        PGvector embeddingVector = getEmbeddingVector(queryText);
        Object[] neighborParams = {embeddingVector, indexName, queryText, embeddingVector, size};

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT " +
                        "    id, " +
                        "    dataset_id, " +
                        "    index_name, " +
                        "    document_title, " +
                        "    document_desc, " +
                        "    document_content, " +
                        "    1 - (document_embedding <=> ?) AS cosine_similarity, " +
                        "    token_size, " +
                        "    doc_chunk, " +
                        "    score, " +
                        "    page, " +
                        "    source_file, " +
                        "    source_url, " +
                        "    source_type, " +
                        "    author " +
                        "FROM " +
                        "    alinesno_search_vector_document " +
                        "WHERE " +
                        "    index_name = ? " +
                        "    AND to_tsvector('english', document_title || ' ' || document_content) @@ plainto_tsquery('english', ?) " +
                        "ORDER BY " +
                        "    document_embedding <=> ? " +
                        "LIMIT " +
                        "    ?;",
                neighborParams);

        return convertToDocumentVectorBeans(rows, quoteLimit);
    }

    @Override
    public List<DocumentVectorBean> queryMultiVectorDocument(List<Long> datasetIdArr, String queryText, Integer size) {

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        PGvector embeddingVector = getEmbeddingVector(queryText);

        String sql = "SELECT * , 1 - (document_embedding <=> :embeddingVector) AS cosine_similarity " +
                "FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " " +
                "WHERE dataset_id IN (:datasetIdArr) " +
                "ORDER BY cosine_similarity DESC LIMIT :size";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("embeddingVector", embeddingVector);
        paramMap.put("datasetIdArr", datasetIdArr);
        paramMap.put("size", size);

        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql, paramMap);

        // 转换结果
        return convertToDocumentVectorBeans(rows, 0);
    }

    @Override
    public List<DocumentVectorBean> queryPageByDatasetIdAndDocumentName(Map<String, Object> params) {
        // 参数校验
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("查询参数不能为空");
        }

        Long datasetId = (Long) params.get("datasetId");
        String documentName = (String) params.get("documentName");
        Integer limit = (Integer) params.get("limit");
        Integer offset = (Integer) params.get("offset");

        if (datasetId == null || documentName == null || limit == null || offset == null) {
            throw new IllegalArgumentException("缺少必要的查询参数");
        }

        // 明确指定查询字段，排除document_embedding
        String sql = "SELECT " +
                "id, " +
                "dataset_id, " +
                "index_name, " +
                "document_title, " +
                "document_desc, " +
                "document_content, " +
                "token_size, " +
                "doc_chunk, " +
                "score, " +
                "page, " +
                "source_file, " +
                "source_url, " +
                "source_type, " +
                "author, " +
                "created_at " +
                "FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " " +
                "WHERE dataset_id = ? AND document_title = ? " +
                "ORDER BY created_at DESC " +
                "LIMIT ? OFFSET ?";

        // 执行查询
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                sql,
                datasetId,
                documentName,
                limit,
                offset
        );

        // 转换结果
        return convertToDocumentVectorBeans(rows, 0);
    }

    @Override
    public long countByDatasetIdAndDocumentName(Long datasetId, String documentName) {
        // 参数校验
        if (datasetId == null || documentName == null) {
            throw new IllegalArgumentException("datasetId和documentName不能为空");
        }

        // 构建SQL查询
        String sql = "SELECT COUNT(1) FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " " +
                "WHERE dataset_id = ? AND document_title = ?";

        // 执行查询并返回结果，处理可能的null值
        Long count = jdbcTemplate.queryForObject(sql, Long.class, datasetId, documentName);
        return count != null ? count : 0L;
    }

    @SneakyThrows
    @Override
    public DocumentVectorBean getVectorById(long segmentId) {
        // 1. 构建SQL查询语句
        String sql = "SELECT *, 0 AS cosine_similarity " + // 添加伪相似度字段以满足convert方法要求
                "FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " " +
                "WHERE id = ?";

        // 2. 执行查询
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, segmentId);

        // 3. 检查结果是否存在
        if (rows.isEmpty()) {
            return null; // 或者抛出异常 throw new RuntimeException("未找到ID为 " + segmentId + " 的文档片段");
        }

        // 4. 使用现有转换方法
        List<DocumentVectorBean> result = convertToDocumentVectorBeans(rows, 0); // quoteLimit设为0表示不限制

        // 5. 返回第一个结果(唯一结果)
        return result.get(0);
    }

    @Override
    public void deleteVectorDocument(Long datasetId, String documentName) {
        // 参数校验
        if (datasetId == null || documentName == null) {
            throw new IllegalArgumentException("datasetId和documentName不能为空");
        }

        // 构建删除SQL
        String deleteSql = "DELETE FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME +
                " WHERE dataset_id = ? AND document_title = ?";

        // 执行删除操作
        int deletedCount = jdbcTemplate.update(deleteSql, datasetId, documentName);

        // 记录日志
        if (deletedCount > 0) {
            log.info("成功删除数据集ID[{}]下文档名为[{}]的{}条向量记录", datasetId, documentName, deletedCount);
        } else {
            log.warn("未找到数据集ID[{}]下文档名为[{}]的向量记录", datasetId, documentName);
        }
    }

    @Override
    public void deleteVectorDataset(String datasetId) {
        // 参数校验
        if (datasetId == null) {
            throw new IllegalArgumentException("datasetId不能为空");
        }

        // 构建删除SQL
        String deleteSql = "DELETE FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME +
                " WHERE dataset_id = ?";

        // 执行删除操作
        int deletedCount = jdbcTemplate.update(deleteSql, Long.parseLong(datasetId));

        // 记录日志
        if (deletedCount > 0) {
            log.info("成功删除数据集ID[{}]{}条向量记录", datasetId, deletedCount);
        } else {
            log.warn("未找到数据集ID[{}]的向量记录", datasetId);
        }
    }
}