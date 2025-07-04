//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alinesno.infra.smart.assistant.plugin.tool.DatabaseTool;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.sql.DataSource;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//public abstract class DatabaseFilterTool extends DatabaseTool {
//
//    // 使用ConcurrentHashMap缓存数据源
//    private static final Map<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();
//
//    @Setter
//    private boolean filterAccount;
//
//    @Setter
//    private boolean filterOrg;
//
//    /**
//     * 查询数据并将结果合并整理为字符串返回
//     * @param querySql 查询 SQL 语句
//     * @return 整理后的查询结果字符串
//     */
//    protected String queryData(String querySql) {
//
//        DataSource dataSource = getDataSource(this.getAccountOrgId());
//
//        if (dataSource == null) {
//            log.error("数据源未成功创建，无法执行查询。");
//            return null;
//        }
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource) ;
//
//        try {
//            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(querySql);
//            StringBuilder resultString = new StringBuilder();
//            for (Map<String, Object> row : resultList) {
//                for (Map.Entry<String, Object> entry : row.entrySet()) {
//                    resultString.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
//                }
//                if (!row.isEmpty()) {
//                    // 去掉最后一个逗号和空格
//                    resultString.delete(resultString.length() - 2, resultString.length());
//                }
//                resultString.append("\n");
//                if (resultString.length() > getMaxResultLength()) {
//                    resultString.setLength(getMaxResultLength());
//                    break;
//                }
//            }
//            return resultString.toString();
//        } catch (Exception e) {
//            System.err.println("执行查询时发生错误: " + e.getMessage());
//            return "执行查询时发生错误: " + e.getMessage();
//        }
//    }
//
//    protected DataSource getDataSource(String accountOrgId) {
//        return dataSourceCache.computeIfAbsent(accountOrgId, id -> {
//            if (getJdbcUrl() == null || getUsername() == null || getPassword() == null) {
//                log.error("数据库连接信息不完整，无法创建数据源。");
//                return null;
//            }
//
//            DruidDataSource dataSource = new DruidDataSource();
//            dataSource.setDriverClassName(getDriverName());
//            dataSource.setUrl(getJdbcUrl());
//            dataSource.setUsername(getUsername());
//            dataSource.setPassword(getPassword());
//
//            dataSource.setConnectionInitSqls(Collections.singletonList(
//                    "SET search_path TO research_" + id
//            ));
//
//            // 合理配置连接池参数
//            dataSource.setInitialSize(1);          // 初始连接数
//            dataSource.setMinIdle(1);             // 最小空闲连接
//            dataSource.setMaxActive(5);           // 最大连接数
//            dataSource.setMaxWait(2000);
//            dataSource.setTimeBetweenEvictionRunsMillis(60000);
//            dataSource.setMinEvictableIdleTimeMillis(300000);
//            dataSource.setTestWhileIdle(true);
//            dataSource.setTestOnBorrow(true);
//            dataSource.setTestOnReturn(false);
//            dataSource.setBreakAfterAcquireFailure(true);
//            dataSource.setConnectionErrorRetryAttempts(0);
//
//            return dataSource;
//        });
//    }
//
//}