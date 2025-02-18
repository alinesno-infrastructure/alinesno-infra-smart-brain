package com.alinesno.infra.base.search.memory.storage;

import com.alinesno.infra.base.search.memory.BaseMemoryStore;
import com.alinesno.infra.base.search.memory.bean.MemoryNode;
import com.alinesno.infra.base.search.memory.bean.MemoryNodeWithScore;
import com.alinesno.infra.base.search.vector.utils.DashScopeEmbeddingUtils;
import com.pgvector.PGvector;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 记忆库处理
 */
@Slf4j
@Service
public class PgVectorMemoryStore implements BaseMemoryStore {

    @Resource(name = "pgvectorJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private DashScopeEmbeddingUtils dashScopeEmbeddingUtils;

    // 变量转换成大写
    private static final String ALINESNO_SEARCH_VECTOR_MEMORY_NODE = "alinesno_search_memory_node";

    @Override
    public void createMemoryVectorIndex() {

        // 调整为只为一个向量库
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        String ddl = "CREATE table IF NOT EXISTS " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + " (\n" +
                "                    memory_id UUID NOT NULL DEFAULT gen_random_uuid(), -- 记忆的唯一标识符，默认使用UUID生成\n" +
                "                    agent_id VARCHAR, -- id，表示记忆所属的用户Id\n" +
                "                    agent_name VARCHAR, -- 用户名，表示记忆所属的用户\n" +
                "                    target_id VARCHAR, -- 目标id，表示被记忆的目标或对象\n" +
                "                    target_name VARCHAR, -- 目标名称，表示被记忆的目标或对象\n" +
                "                    content TEXT, -- 记忆的内容\n" +
                "                    keys VARCHAR, -- 记忆的键(有多个key存在)\n" +
                "                    keys_vector VECTOR (1536), -- 记忆的向量表示，用于向量搜索和相似度计算\n" +
                "                    value VARCHAR, -- 记忆的值\n" +
                "                    score_recall DOUBLE PRECISION, -- 记忆的召回分数\n" +
                "                    score_rank DOUBLE PRECISION, -- 记忆的排序分数\n" +
                "                    score_rerank DOUBLE PRECISION, -- 记忆的重排序分数\n" +
                "                    memory_type VARCHAR, -- 记忆的类型\n" +
                "                    action_status VARCHAR DEFAULT 'none', -- 动作状态，表示记忆的当前动作状态，默认为\"none\"\n" +
                "                    store_status VARCHAR DEFAULT 'valid', -- 存储状态，表示记忆的存储状态，默认为\"valid\"\n" +
                "                    content_vector VECTOR (1536), -- 记忆的向量表示，可能与keyVector有区别，具体视业务逻辑而定\n" +
                "                    timestamp BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW()), -- 记忆的时间戳，确保每个记忆节点的时间戳唯一\n" +
                "                    dt DATE, -- 记忆的日期时间字符串，格式为\"yyyy-MM-dd\"\n" +
                "                    obs_reflected INTEGER DEFAULT 0, -- 观察到的反射次数，用于记录记忆被反射的次数\n" +
                "                    obs_updated INTEGER DEFAULT 0, -- 观察到的更新次数，用于记录记忆被更新的次数\n" +
                "                    PRIMARY KEY (memory_id)\n" +
                "                )";

        jdbcTemplate.execute(ddl);
        log.debug("创建记忆库表成功");

        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_content_vector ON " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + "  USING ivfflat (content_vector vector_cosine_ops) WITH (lists = 100)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_keys_vector ON " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + " USING ivfflat (keys_vector vector_cosine_ops) WITH (lists = 100)");

        jdbcTemplate.execute("COMMENT ON TABLE " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + " IS '该表用于存储记忆节点，包括记忆内容、元数据、向量表示、以及各种评分和状态信息'");

        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".memory_id IS '记忆的唯一标识符，默认使用UUID生成'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".agent_id IS '用户名，表示记忆所属的用户Id'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".agent_name IS '用户名，表示记忆所属的用户'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".target_id IS '目标id，表示被记忆的目标或对象'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".target_name IS '目标名称，表示被记忆的目标或对象'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".content IS '记忆的内容'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".keys IS '记忆的键(有多个key存在)'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".keys_vector IS '记忆的向量表示，用于向量搜索和相似度计算'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".value IS '记忆的值'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".score_recall IS '记忆的召回分数'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".score_rank IS '记忆的排序分数'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".score_rerank IS '记忆的重排序分数'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".memory_type IS '记忆的类型'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".action_status IS '动作状态，表示记忆的当前动作状态，默认为\"none\"'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".store_status IS '存储状态，表示记忆的存储状态，默认为\"valid\"'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".content_vector IS '记忆的向量表示，可能与keyVector有区别，具体视业务逻辑而定'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".timestamp IS '记忆的时间戳，确保每个记忆节点的时间戳唯一'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".dt IS '记忆的日期时间字符串，格式为\"yyyy-MM-dd\"'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".obs_reflected IS '观察到的反射次数，用于记录记忆被反射的次数'");
        jdbcTemplate.execute("COMMENT ON COLUMN " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE + ".obs_updated IS '观察到的更新次数，用于记录记忆被更新的次数'");

    }

    @SneakyThrows
    @Override
    public void batchInsert(List<MemoryNode> nodes) {
        String sql = "INSERT INTO " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE +
                " (" +
                "   memory_id, " +
                "   agent_id, " +
                "   agent_name, " +
                "   target_id, " +
                "   target_name, " +
                "   content, " +
                "   keys, " +
                "   keys_vector, " +
                "   value, " +
                "   score_recall, " +
                "   score_rank, " +
                "   score_rerank, " +
                "   memory_type, " +
                "   action_status, " +
                "   store_status, " +
                "   content_vector, " +
                "   timestamp, " +
                "   dt, " +
                "   obs_reflected, " +
                "   obs_updated" +
                ") VALUES (" +
                "   gen_random_uuid(), " +
                "   :agentId, " +
                "   :agentName, " +
                "   :targetId, " +
                "   :targetName, " +
                "   :content, " +
                "   :keys, " +
                "   :keysVector, " +
                "   :value, " +
                "   :scoreRecall, " +
                "   :scoreRank, " +
                "   :scoreRerank, " +
                "   :memoryType, " +
                "   :actionStatus, " +
                "   :storeStatus, " +
                "   :contentVector, " +
                "   EXTRACT(epoch FROM now()), " +
                "   :dt, " +
                "   :obsReflected, " +
                "   :obsUpdated" +
                ")";

        List<SqlParameterSource> batchValues = new ArrayList<>();

        for (MemoryNode node : nodes) {

            List<Double> keysVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(node.getKeys());
            List<Double> contentVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(node.getContent());

            // 将文本向量字符串转换为 PGvector 对象
            PGvector keysPGVector = new PGvector(keysVector);
            PGvector contentPGVector = new PGvector(contentVector);

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("agentId", node.getAgentId());
            parameterSource.addValue("agentName", node.getAgentName());
            parameterSource.addValue("targetId", node.getTargetId());
            parameterSource.addValue("targetName", node.getTargetName());
            parameterSource.addValue("content", node.getContent());
            parameterSource.addValue("keys", node.getKeys());
            parameterSource.addValue("keysVector", keysPGVector) ;
            parameterSource.addValue("value", node.getValue());
            parameterSource.addValue("scoreRecall", node.getScoreRecall());
            parameterSource.addValue("scoreRank", node.getScoreRank());
            parameterSource.addValue("scoreRerank", node.getScoreRerank());
            parameterSource.addValue("memoryType", node.getMemoryType());
            parameterSource.addValue("actionStatus", node.getActionStatus());
            parameterSource.addValue("storeStatus", node.getStoreStatus());
            parameterSource.addValue("contentVector", contentPGVector) ;
            parameterSource.addValue("dt", LocalDate.now()); // 使用当前日期
            parameterSource.addValue("obsReflected", node.getObsReflected());
            parameterSource.addValue("obsUpdated", node.getObsUpdated());

            batchValues.add(parameterSource);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, batchValues.toArray(new SqlParameterSource[0]));
    }

    @SneakyThrows
    @Override
    public List<MemoryNodeWithScore> searchByAgentAndVector(Long agentId, Long targetId , String queryText, int limit) {

        // 文本向量化
        List<Double> queryVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(queryText);
        PGvector queryPGVector = new PGvector(queryVector);

        // SQL 查询
        String sql = "SELECT *," +
                " GREATEST(1 - (keys_vector <=> :queryVector), 1 - (content_vector <=> :queryVector)) AS similarity " +
                "FROM " + ALINESNO_SEARCH_VECTOR_MEMORY_NODE +
                " WHERE agent_id = :agentId " +
                " AND target_id = :targetId " +
                " ORDER BY similarity DESC, timestamp DESC " +
                " LIMIT :limit";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("agentId", agentId.toString());
        parameters.addValue("targetId", targetId.toString());
        parameters.addValue("queryVector", queryPGVector);
        parameters.addValue("limit", limit);

        return namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> {
            MemoryNode memoryNode = mapRowToMemoryNode(rs);
            double similarity = rs.getDouble("similarity");

            MemoryNodeWithScore node = new MemoryNodeWithScore(memoryNode.getContent() , similarity);

            node.setTargetId(memoryNode.getTargetId());
            node.setTargetName(memoryNode.getTargetName());
            node.setKeys(memoryNode.getKeys());

            return node ;
        });
    }
    private MemoryNode mapRowToMemoryNode(ResultSet rs) throws SQLException {
        MemoryNode node = new MemoryNode();
        node.setAgentId(Long.parseLong(rs.getString("agent_id")));
        node.setAgentName(rs.getString("agent_name"));
        node.setTargetId(rs.getString("target_id"));
        node.setTargetName(rs.getString("target_name"));
        node.setContent(rs.getString("content"));
        node.setKeys(rs.getString("keys"));
        node.setValue(rs.getString("value"));
        node.setScoreRecall(rs.getDouble("score_recall"));
        node.setScoreRank(rs.getDouble("score_rank"));
        node.setScoreRerank(rs.getDouble("score_rerank"));
        node.setMemoryType(rs.getString("memory_type"));
        node.setActionStatus(rs.getString("action_status"));
        node.setStoreStatus(rs.getString("store_status"));
        node.setObsReflected(rs.getInt("obs_reflected"));
        node.setObsUpdated(rs.getInt("obs_updated"));
        return node;
    }

}
