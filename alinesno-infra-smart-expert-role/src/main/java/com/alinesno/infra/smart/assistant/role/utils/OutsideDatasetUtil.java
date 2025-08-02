package com.alinesno.infra.smart.assistant.role.utils;

import com.agentsflex.core.document.Document;
import com.agentsflex.core.store.SearchWrapper;
import com.agentsflex.store.pgvector.PgvectorVectorStore;
import com.agentsflex.store.pgvector.PgvectorVectorStoreConfig;
import com.alinesno.infra.base.search.vector.utils.DashScopeEmbeddingUtils;
import com.alinesno.infra.smart.im.dto.ParserDataBean;
import com.alinesno.infra.smart.scene.constants.SceneConstants;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 外部知识库工具类
 */
@Slf4j
@Component
public class OutsideDatasetUtil {

    @Autowired
    private DashScopeEmbeddingUtils dashScopeEmbeddingUtils;

    @Value("${spring.datasource.dynamic.datasource.postgresql.url:jdbc:postgresql://127.0.0.1:5432/postgres}")
    private String pgVectorUrl ;

    @Value("${spring.datasource.dynamic.datasource.postgresql.username:test}")
    private String pgVectorUsername;

    @Value("${spring.datasource.dynamic.datasource.postgresql.password:123456}")
    private String pgVectorPassword;

    private String pgVectorHost;

    private int pgVectorPort;

    private String pgVectorDatabase ;

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

//            log.info("解析PostgreSQL连接信息 - Host: {}, Port: {}", pgVectorHost, pgVectorPort);
        } catch (Exception e) {
            log.error("解析PostgreSQL URL失败，使用默认值", e);
            this.pgVectorHost = "127.0.0.1";
            this.pgVectorPort = 5432;
        }
    }

    /**
     * 搜索外部知识库
     *
     * @param collectionIndexName
     * @param goal
     * @param indexDatasetMap
     * @return
     */
    @SneakyThrows
    public String search(String collectionIndexName, String goal, List<ParserDataBean> indexDatasetMap) {
        PgvectorVectorStore store = getPgvectorVectorStore(collectionIndexName);

        List<Double> embeddingVector = dashScopeEmbeddingUtils.getEmbeddingDoubles(goal);
        double[] vector = embeddingVector.stream().mapToDouble(Double::doubleValue).toArray();

        SearchWrapper searchWrapper = new SearchWrapper().text(goal);
        searchWrapper.setVector(vector);
        searchWrapper.setMinScore(0.2);
        searchWrapper.setOutputVector(true);
        List<Document> docs = store.search(searchWrapper);

        // 获取到解析的文件信息
        StringBuilder output = new StringBuilder();

        if(CollectionUtils.isNotEmpty(docs)){
            int sort = 1 ;
            for(Document document : docs){
                log.info("文档信息: {}", document.getMetadataMap());
                Map<String, Object> metadataMap = document.getMetadataMap() ;

                String fileName = metadataMap.get("fileName") + "" ;
                output.append("文件名称: ").append(fileName).append("\n").append("文件内容:").append(document.getContent()).append("\n");

                if(indexDatasetMap != null){
                    ParserDataBean parserDataBean = new ParserDataBean(fileName, sort) ;
                    indexDatasetMap.add(parserDataBean) ;
                    sort ++ ;
                }
            }
        }

        // TODO indexDatasetMap 做名称去重
        return output.toString() ;
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
