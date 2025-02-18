//package com.alinesno.infra.base.search.vector.pgvector.impl;
//
//import cn.hutool.core.util.IdUtil;
//import com.alinesno.infra.base.search.vector.DocumentVectorBean;
//import com.alinesno.infra.base.search.vector.dto.VectorSearchDto;
//import com.alinesno.infra.base.search.vector.service.IPgVectorService;
//import com.alinesno.infra.base.search.vector.utils.DashScopeEmbeddingUtils;
//import com.pgvector.PGvector;
//import jakarta.annotation.Resource;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author luoxiaodong
// * @version 1.0.0
// */
//@Slf4j
//@Component
//public class PgVectorServiceImpl implements IPgVectorService {
//
//    // 变量转换成大写
//    private static final String ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME = "alinesno_search_vector_document";
//
//    @Resource(name = "pgvectorJdbcTemplate")
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private DashScopeEmbeddingUtils dashScopeEmbeddingUtils;
//
//    @SneakyThrows
//    @Override
//    public List<DocumentVectorBean> queryDocument(String indexName, String fileName, String queryText, int size) {
//
//        // 获取查询向量
//        List<Double> embeddingVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(queryText);
//        // 查询语句
//        String querySql = "SELECT *, 1 - (document_embedding <=> ?) AS cosine_similarity " +
//                "FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME +
//                "WHERE index_name=? " +
//                "AND source_file = ? " +
//                "AND (document_title ILIKE ? OR document_content ILIKE ?) " +
//                "ORDER BY cosine_similarity DESC LIMIT ?";
//
//        // 构建查询条件中的 LIKE 语句
//        String likeCondition = "%" + queryText + "%";
//        Object[] queryParams = new Object[] { indexName, fileName, likeCondition, likeCondition, new PGvector(embeddingVector), size };
//
//        // 执行查询
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(querySql, queryParams);
//
//        // 将查询结果转换为 DocumentVectorBean 对象列表
//        List<DocumentVectorBean> results = new ArrayList<>();
//        for (Map<String, Object> row : rows) {
//            DocumentVectorBean documentVectorBean = new DocumentVectorBean();
//            documentVectorBean.setId((Long) row.get("id"));
//            documentVectorBean.setDataset_id((Long) row.get("dataset_id"));
//            documentVectorBean.setIndexName((String) row.get("index_name"));
//            documentVectorBean.setDocument_title((String) row.get("document_title"));
//            documentVectorBean.setDocument_desc((String) row.get("document_desc"));
//            documentVectorBean.setDocument_content((String) row.get("document_content"));
//            documentVectorBean.setDocument_embedding((String) row.get("document_embedding"));
//            documentVectorBean.setScore(((Number) row.get("cosine_similarity")).floatValue());
//            documentVectorBean.setPage(((Number) row.get("page")).intValue());
//            documentVectorBean.setDoc_chunk((String) row.get("doc_chunk"));
//            documentVectorBean.setToken_size(((Number) row.get("token_size")).intValue());
//            documentVectorBean.setSourceFile((String) row.get("source_file"));
//            documentVectorBean.setSourceUrl((String) row.get("source_url"));
//            documentVectorBean.setSourceType((String) row.get("source_type"));
//            documentVectorBean.setAuthor((String) row.get("author"));
//
//            results.add(documentVectorBean);
//        }
//
//        return results;
//    }
//
//    @SneakyThrows
//    @Override
//    public List<DocumentVectorBean> queryVectorDocument(String indexName, String queryText, int size) {
//
//        List<Double> embeddingVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(queryText);
//        Object[] neighborParams = new Object[] {
//                new PGvector(embeddingVector) ,
//                indexName ,
//                size
//        };
//
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
//                "SELECT * , 1 - (document_embedding <=> ?) AS cosine_similarity " +
//                        "FROM "+ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME+" " +
//                        "WHERE index_name=? " +
//                        "ORDER BY cosine_similarity DESC LIMIT ?", neighborParams );
//
//        // 将查询结果转换为 DocumentVectorBean 对象列表
//        List<DocumentVectorBean> results = new ArrayList<>();
//
//        for (Map<String, Object> row : rows) {
//
//            DocumentVectorBean documentVectorBean = new DocumentVectorBean();
//            documentVectorBean.setId((Long) row.get("id"));
//            documentVectorBean.setDataset_id((Long) row.get("dataset_id"));
//            documentVectorBean.setIndexName((String) row.get("index_name"));
//            documentVectorBean.setDocument_title((String) row.get("document_title"));
//            documentVectorBean.setDocument_desc((String) row.get("document_desc"));
//            documentVectorBean.setDocument_content((String) row.get("document_content"));
//            documentVectorBean.setScore(((Number) row.get("cosine_similarity")).floatValue());
//            documentVectorBean.setPage(((Number) row.get("page")).intValue());
//            documentVectorBean.setDoc_chunk((String) row.get("doc_chunk"));
//            documentVectorBean.setToken_size(((Number) row.get("token_size")).intValue());
//            documentVectorBean.setSourceFile((String) row.get("source_file"));
//            documentVectorBean.setSourceUrl((String) row.get("source_url"));
//            documentVectorBean.setSourceType((String) row.get("source_type"));
//            documentVectorBean.setAuthor((String) row.get("author"));
//
//            results.add(documentVectorBean);
//        }
//
//          return results;
//    }
//
//    @SneakyThrows
//    @Override
//    public List<DocumentVectorBean> queryVectorDocument(VectorSearchDto dto) {
//
//        String indexName = dto.getCollectionName() ;
//        String queryText = dto.getSearchText() ;
//        int size = dto.getTopK() ;
//
//        double minRelevance = Double.parseDouble(dto.getMinRelevance()) ;
//        int quoteLimit = dto.getQuoteLimit(); // 获取最大字符总和限制
//
//        List<Double> embeddingVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(queryText);
//        Object[] neighborParams = new Object[] {
//                new PGvector(embeddingVector) ,
//                indexName ,
//                minRelevance ,
//                size
//        };
//
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
//                "WITH similarity_cte AS (" +
//                        "SELECT *, 1 - (document_embedding <=> ?) AS cosine_similarity " +
//                        "FROM " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " " +
//                        "WHERE index_name = ?" +
//                        ") " +
//                        "SELECT * FROM similarity_cte " +
//                        "WHERE cosine_similarity >= ? " +
//                        "ORDER BY cosine_similarity DESC LIMIT ?", neighborParams);
//
//        // 将查询结果转换为 DocumentVectorBean 对象列表
//        List<DocumentVectorBean> results = new ArrayList<>();
//        int totalContentLength = 0;
//
//        for (Map<String, Object> row : rows) {
//
//            String documentContent = (String) row.get("document_content");
//            if (documentContent == null) {
//                documentContent = "";
//            }
//            int currentLength = documentContent.length();
//
//            if (totalContentLength + currentLength <= quoteLimit) {
//                DocumentVectorBean documentVectorBean = new DocumentVectorBean();
//                documentVectorBean.setId((Long) row.get("id"));
//                documentVectorBean.setDataset_id((Long) row.get("dataset_id"));
//                documentVectorBean.setIndexName((String) row.get("index_name"));
//                documentVectorBean.setDocument_title((String) row.get("document_title"));
//                documentVectorBean.setDocument_desc((String) row.get("document_desc"));
//                documentVectorBean.setDocument_content((String) row.get("document_content"));
//                documentVectorBean.setScore(((Number) row.get("cosine_similarity")).floatValue());
//                documentVectorBean.setPage(((Number) row.get("page")).intValue());
//                documentVectorBean.setDoc_chunk((String) row.get("doc_chunk"));
//                documentVectorBean.setToken_size(((Number) row.get("token_size")).intValue());
//                documentVectorBean.setSourceFile((String) row.get("source_file"));
//                documentVectorBean.setSourceUrl((String) row.get("source_url"));
//                documentVectorBean.setSourceType((String) row.get("source_type"));
//                documentVectorBean.setAuthor((String) row.get("author"));
//
//                results.add(documentVectorBean);
//                totalContentLength += currentLength;
//            } else {
//                break;
//            }
//        }
//
//        return results;
//    }
//
//    @Override
//    public void createVectorIndex(String indexName) {
//
//        // 调整为只为一个向量库
//        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
//        String ddl = "CREATE TABLE IF NOT EXISTS "+ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME+" (\n" +
//                "    id BIGSERIAL PRIMARY KEY,\n" +
//                "    dataset_id BIGINT NOT NULL,\n" +
//                "    index_name VARCHAR(255) NOT NULL,\n" +
//                "    document_title TEXT,\n" +
//                "    document_desc text,  \n" +
//                "    document_content TEXT,\n" +
//                "    document_embedding VECTOR (1536),\n" +
//                "    token_size int,  \n" +
//                "    doc_chunk text,  \n" +
//                "    score REAL,\n" +
//                "    page INTEGER,\n" +
//                "    source_file VARCHAR(255),\n" +
//                "    source_url TEXT,\n" +
//                "    source_type VARCHAR(50),\n" +
//                "    author VARCHAR(255)\n" +
//                ");"  ;
//
//        jdbcTemplate.execute(ddl) ;
//        jdbcTemplate.execute("CREATE INDEX ON "+ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME+" USING ivfflat (document_embedding vector_cosine_ops) WITH (lists = 100);");
//        jdbcTemplate.execute("CREATE INDEX idx_fts_"+ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME+" ON "+ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME+" USING gin(to_tsvector('english', document_title || ' ' || document_content));") ;
//    }
//
//    @SneakyThrows
//    @Override
//    public void insertVector(DocumentVectorBean documentVectorBean) {
//
//        String indexName = documentVectorBean.getIndexName() ;
//
//        // 将文本向量字符串转换为 PGvector 对象
//        List<Double> embeddingVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(documentVectorBean.getDocument_content());
//        Object[] insertParams = getObjects(documentVectorBean, embeddingVector);
//
//        // 执行插入操作
//        int count = jdbcTemplate.update(
//                "INSERT INTO "+ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME+" " +
//                        "(" +
//                            "id, " +
//                            "dataset_id, " +
//                            "index_name, " +
//                            "document_title, " +
//                            "document_desc, " +
//                            "document_content, " +
//                            "document_embedding, " +
//                            "token_size, " +
//                            "doc_chunk, " +
//                            "score, " +
//                            "page, " +
//                            "source_file, " +
//                            "source_url, " +
//                            "source_type, " +
//                            "author" +
//                        ") " +
//                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
//                insertParams
//        );
//        log.debug("插入了 " + count + " 条记录");
//    }
//
//    @NotNull
//    private static Object[] getObjects(DocumentVectorBean documentVectorBean, List<Double> embeddingVector) {
//
//        PGvector pgVector = new PGvector(embeddingVector) ;
//
//        // 准备插入参数
//        return new Object[] {
//                IdUtil.getSnowflakeNextId(),
//                documentVectorBean.getDataset_id(),
//                documentVectorBean.getIndexName(),
//                documentVectorBean.getDocument_title(),
//                documentVectorBean.getDocument_desc(),
//                documentVectorBean.getDocument_content(),
//                pgVector,
//                documentVectorBean.getToken_size(),
//                documentVectorBean.getDoc_chunk(),
//                documentVectorBean.getScore(),
//                documentVectorBean.getPage(),
//                documentVectorBean.getSourceFile(),
//                documentVectorBean.getSourceUrl(),
//                documentVectorBean.getSourceType(),
//                documentVectorBean.getAuthor()
//        };
//    }
//
//    @SneakyThrows
//    @Override
//    public void updateVector(DocumentVectorBean documentVectorBean) {
//        String indexName = documentVectorBean.getIndexName();
//
//        List<Double> embeddingVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(documentVectorBean.getDocument_content());
//        Object[] updateParams = getObjects(documentVectorBean, embeddingVector);
//
//        int count = jdbcTemplate.update(
//                "UPDATE " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " SET " +
//                        "dataset_id = ?, " +
//                        "index_name = ?, " +
//                        "document_title = ?, " +
//                        "document_desc = ?, " +
//                        "document_content = ?, " +
//                        "document_embedding = ?, " +
//                        "token_size = ?, " +
//                        "doc_chunk = ?, " +
//                        "score = ?, " +
//                        "page = ?, " +
//                        "source_file = ?, " +
//                        "source_url = ?, " +
//                        "source_type = ?, " +
//                        "author = ? " +
//                        "WHERE id = ?",
//                updateParams[1], // dataset_id
//                updateParams[2], // index_name
//                updateParams[3], // document_title
//                updateParams[4], // document_desc
//                updateParams[5], // document_content
//                updateParams[6], // document_embedding
//                updateParams[7], // token_size
//                updateParams[8], // doc_chunk
//                updateParams[9], // score
//                updateParams[10], // page
//                updateParams[11], // source_file
//                updateParams[12], // source_url
//                updateParams[13], // source_type
//                updateParams[14], // author
//                documentVectorBean.getId() // id
//        );
//
//        log.debug("更新了 " + count + " 条记录");
//    }
//
//    @Override
//    public void deleteVectorIndex(String indexName, long documentId) {
//        int count = jdbcTemplate.update(
//                "DELETE FROM " + indexName + " WHERE id = ?",
//                documentId
//        );
//
//        log.debug("删除了 " + count + " 条记录");
//    }
//
//    @Override
//    public List<DocumentVectorBean> queryFullTextDocument(VectorSearchDto dto) {
//        String indexName = dto.getCollectionName();
//        String queryText = dto.getSearchText();
//        int size = dto.getTopK();
//        int quoteLimit = dto.getQuoteLimit(); // 获取最大字符总和限制
//
//        Object[] params = new Object[]{indexName, queryText, size};
//
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
//                "SELECT " +
//                        "    id, " +
//                        "    dataset_id, " +
//                        "    index_name, " +
//                        "    document_title, " +
//                        "    document_desc, " +
//                        "    document_content, " +
//                        "    token_size, " +
//                        "    doc_chunk, " +
//                        "    score, " +
//                        "    page, " +
//                        "    source_file, " +
//                        "    source_url, " +
//                        "    source_type, " +
//                        "    author " +
//                        "FROM " +
//                        "    alinesno_search_vector_document " +
//                        "WHERE " +
//                        "    index_name = ? " +
//                        "    AND to_tsvector('english', document_title || ' ' || document_content) @@ plainto_tsquery('english', ?) " +
//                        "ORDER BY " +
//                        "    ts_rank(to_tsvector('english', document_title || ' ' || document_content), plainto_tsquery('english', ?)) DESC " +
//                        "LIMIT " +
//                        "    ?;",
//                params);
//
//        // 将查询结果转换为 DocumentVectorBean 对象列表
//        List<DocumentVectorBean> results = new ArrayList<>();
//        int totalContentLength = 0;
//
//        for (Map<String, Object> row : rows) {
//            String documentContent = (String) row.get("document_content");
//            if (documentContent == null) {
//                documentContent = "";
//            }
//            int currentLength = documentContent.length();
//
//            if (totalContentLength + currentLength <= quoteLimit) {
//                DocumentVectorBean documentVectorBean = new DocumentVectorBean();
//                documentVectorBean.setId((Long) row.get("id"));
//                documentVectorBean.setDataset_id((Long) row.get("dataset_id"));
//                documentVectorBean.setIndexName((String) row.get("index_name"));
//                documentVectorBean.setDocument_title((String) row.get("document_title"));
//                documentVectorBean.setDocument_desc((String) row.get("document_desc"));
//                documentVectorBean.setDocument_content((String) row.get("document_content"));
//                // 这里没有余弦相似度了，可以根据需要调整 score 的设置逻辑
//                documentVectorBean.setScore(0f);
//                documentVectorBean.setPage(((Number) row.get("page")).intValue());
//                documentVectorBean.setDoc_chunk((String) row.get("doc_chunk"));
//                documentVectorBean.setToken_size(((Number) row.get("token_size")).intValue());
//                documentVectorBean.setSourceFile((String) row.get("source_file"));
//                documentVectorBean.setSourceUrl((String) row.get("source_url"));
//                documentVectorBean.setSourceType((String) row.get("source_type"));
//                documentVectorBean.setAuthor((String) row.get("author"));
//
//                results.add(documentVectorBean);
//                totalContentLength += currentLength;
//            } else {
//                break;
//            }
//        }
//
//        return results;
//    }
//
//    @SneakyThrows
//    @Override
//    public List<DocumentVectorBean> queryHybridDocument(VectorSearchDto dto) {
//        String indexName = dto.getCollectionName();
//        String queryText = dto.getSearchText();
//        int size = dto.getTopK();
//        int quoteLimit = dto.getQuoteLimit(); // 获取最大字符总和限制
//
//        List<Double> embeddingVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(queryText);
//        Object[] neighborParams = new Object[]{
//                new PGvector(embeddingVector),
//                indexName,
//                queryText,
//                new PGvector(embeddingVector),
//                size
//        };
//
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
//                "SELECT " +
//                        "    id, " +
//                        "    dataset_id, " +
//                        "    index_name, " +
//                        "    document_title, " +
//                        "    document_desc, " +
//                        "    document_content, " +
//                        "    1 - (document_embedding <=> ?) AS cosine_similarity, " +
//                        "    token_size, " +
//                        "    doc_chunk, " +
//                        "    score, " +
//                        "    page, " +
//                        "    source_file, " +
//                        "    source_url, " +
//                        "    source_type, " +
//                        "    author " +
//                        "FROM " +
//                        "    alinesno_search_vector_document " +
//                        "WHERE " +
//                        "    index_name = ? " +
//                        "    AND to_tsvector('english', document_title || ' ' || document_content) @@ plainto_tsquery('english', ?) " +
//                        "ORDER BY " +
//                        "    document_embedding <=> ? " +
//                        "LIMIT " +
//                        "    ?;",
//                neighborParams);
//
//        // 将查询结果转换为 DocumentVectorBean 对象列表
//        List<DocumentVectorBean> results = new ArrayList<>();
//        int totalContentLength = 0;
//
//        for (Map<String, Object> row : rows) {
//            String documentContent = (String) row.get("document_content");
//            if (documentContent == null) {
//                documentContent = "";
//            }
//            int currentLength = documentContent.length();
//
//            if (totalContentLength + currentLength <= quoteLimit) {
//                DocumentVectorBean documentVectorBean = new DocumentVectorBean();
//                documentVectorBean.setId((Long) row.get("id"));
//                documentVectorBean.setDataset_id((Long) row.get("dataset_id"));
//                documentVectorBean.setIndexName((String) row.get("index_name"));
//                documentVectorBean.setDocument_title((String) row.get("document_title"));
//                documentVectorBean.setDocument_desc((String) row.get("document_desc"));
//                documentVectorBean.setDocument_content((String) row.get("document_content"));
//                documentVectorBean.setScore(((Number) row.get("cosine_similarity")).floatValue());
//                documentVectorBean.setPage(((Number) row.get("page")).intValue());
//                documentVectorBean.setDoc_chunk((String) row.get("doc_chunk"));
//                documentVectorBean.setToken_size(((Number) row.get("token_size")).intValue());
//                documentVectorBean.setSourceFile((String) row.get("source_file"));
//                documentVectorBean.setSourceUrl((String) row.get("source_url"));
//                documentVectorBean.setSourceType((String) row.get("source_type"));
//                documentVectorBean.setAuthor((String) row.get("author"));
//
//                results.add(documentVectorBean);
//                totalContentLength += currentLength;
//            } else {
//                break;
//            }
//        }
//
//        return results;
//    }
//
//}

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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private PGvector getEmbeddingVector(String queryText) {
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

    @Override
    public void createVectorIndex(String indexName) {
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        String ddl = "CREATE TABLE IF NOT EXISTS " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " (\n" +
                "    id BIGSERIAL PRIMARY KEY,\n" +
                "    dataset_id BIGINT NOT NULL,\n" +
                "    index_name VARCHAR(255) NOT NULL,\n" +
                "    document_title TEXT,\n" +
                "    document_desc text,  \n" +
                "    document_content TEXT,\n" +
                "    document_embedding VECTOR (1536),\n" +
                "    token_size int,  \n" +
                "    doc_chunk text,  \n" +
                "    score REAL,\n" +
                "    page INTEGER,\n" +
                "    source_file VARCHAR(255),\n" +
                "    source_url TEXT,\n" +
                "    source_type VARCHAR(50),\n" +
                "    author VARCHAR(255)\n" +
                ");";

        jdbcTemplate.execute(ddl);
        jdbcTemplate.execute("CREATE INDEX ON " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " USING ivfflat (document_embedding vector_cosine_ops) WITH (lists = 100);");
        jdbcTemplate.execute("CREATE INDEX idx_fts_" + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " ON " + ALINESNO_SEARCH_VECTOR_DOCUMENT_INDEX_NAME + " USING gin(to_tsvector('english', document_title || ' ' || document_content));");
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
}