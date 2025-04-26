package com.alinesno.infra.smart.assistant.plugin.tool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.annotation.ParamInfo;
import com.alinesno.infra.smart.assistant.annotation.ToolInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 集成MCP工具服务能力
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class DatabaseTool extends Tool {

    private String driverName;
    private String jdbcUrl;
    private String username;
    private String password;
    private String[] tables;
    private String tablePrefix;
    private String tableSuffix;
    private int maxResultLength = 5000; // 添加字段用于配置最大结果长度

    protected DataSource getDataSource() {
        if (jdbcUrl == null || username == null || password == null) {
            log.error("数据库连接信息不完整，无法创建数据源。");
            return null;
        }
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setMaxWait(100);
        dataSource.setBreakAfterAcquireFailure(true); // 连接失败后中断
        dataSource.setConnectionErrorRetryAttempts(0); // 不重试
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);

        return dataSource;
    }

    /**
     * 查询数据并将结果合并整理为字符串返回
     * @param querySql 查询 SQL 语句
     * @return 整理后的查询结果字符串
     */
    protected String queryData(String querySql) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        DataSource dataSource = getDataSource();
        if (dataSource == null) {
            log.error("数据源未成功创建，无法执行查询。");
            return null;
        }
        jdbcTemplate.setDataSource(dataSource);
        try {
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(querySql);
            StringBuilder resultString = new StringBuilder();
            for (Map<String, Object> row : resultList) {
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    resultString.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
                }
                if (!row.isEmpty()) {
                    // 去掉最后一个逗号和空格
                    resultString.delete(resultString.length() - 2, resultString.length());
                }
                resultString.append("\n");
                if (resultString.length() > maxResultLength) {
                    resultString.setLength(maxResultLength);
                    break;
                }
            }
            return resultString.toString();
        } catch (Exception e) {
            System.err.println("执行查询时发生错误: " + e.getMessage());
            return "执行查询时发生错误: " + e.getMessage();
        }
    }

    @SneakyThrows
    @Override
    public String toJson() {
        ToolInfo toolInfo = getClass().getAnnotation(ToolInfo.class);
        String toolName = toolInfo.name().isEmpty() ? getClass().getSimpleName() : toolInfo.name();

        JSONObject toolJson = new JSONObject();
        toolJson.put("name", toolName);
        try {
            toolJson.put("databaseInfo", getDatabaseInfo());
            toolJson.put("table Structures", getAllTableStructures(getDataSource()));
        } catch (SQLException e) {
            log.error("获取数据库信息或表结构时发生错误: " + e.getMessage());
            throw new SQLException("获取数据库信息或表结构时发生错误: " + e.getMessage());
        }
        toolJson.put("description", toolInfo.description());

        List<JSONObject> paramDescriptions = getJsonObjects();

        toolJson.put("paramDescription", paramDescriptions);
        return JSON.toJSONString(toolJson);
    }

    private JSONObject getDatabaseInfo() throws SQLException {
        DataSource dataSource = getDataSource();

        try {
            Connection connection = dataSource.getConnection() ;
            DatabaseMetaData metaData = connection.getMetaData();
            JSONObject databaseInfo = new JSONObject();
            databaseInfo.put("databaseProductName", metaData.getDatabaseProductName());
            databaseInfo.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            return databaseInfo;
        } catch (Exception e) {
            log.error("获取数据库信息时发生错误: " + e.getMessage());
            throw e;
        }
    }

    @NotNull
    private List<JSONObject> getJsonObjects() {
        List<JSONObject> paramDescriptions = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            ParamInfo paramInfo = field.getAnnotation(ParamInfo.class);
            if (paramInfo != null) {
                JSONObject paramJson = new JSONObject();
                paramJson.put("name", paramInfo.name().isEmpty() ? field.getName() : paramInfo.name());
                paramJson.put("description", paramInfo.description());
                paramJson.put("type", paramInfo.type().isEmpty() ? field.getType().getSimpleName() : paramInfo.type());
                paramDescriptions.add(paramJson);
            }
        }
        return paramDescriptions;
    }

    /**
     * 通过数据源获取指定表的结构信息
     *
     * @param tableName 表名
     * @return 表结构信息的 JSON 对象
     * @throws SQLException 处理数据库操作时可能出现的异常
     */
    public JSONObject getTableStructure(String tableName, Connection connection) throws SQLException {
        JSONObject tableJson = new JSONObject();
        tableJson.put("tableName", tableName);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = null;
        ResultSet columns = null;
        try {
            // 获取表注释
            tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            if (tables.next()) {
                String tableComment = tables.getString("REMARKS");
                tableJson.put("tableComment", tableComment);
            }

            JSONArray columnsArray = new JSONArray();
            columns = metaData.getColumns(null, null, tableName, null);
            while (columns.next()) {
                JSONObject columnJson = new JSONObject();
                columnJson.put("columnName", columns.getString("COLUMN_NAME"));
                columnJson.put("dataType", columns.getString("TYPE_NAME"));
                columnJson.put("columnSize", columns.getInt("COLUMN_SIZE"));
                columnJson.put("nullable", columns.getString("IS_NULLABLE"));
                // 获取字段注释
                String columnComment = columns.getString("REMARKS");
                columnJson.put("columnComment", columnComment);
                columnsArray.add(columnJson);
            }
            tableJson.put("columns", columnsArray);
        } finally {
            // 确保 ResultSet 关闭
            if (tables != null) {
                try {
                    tables.close();
                } catch (SQLException e) {
                    log.error("Error closing ResultSet: " + e.getMessage());
                }
            }
            if (columns != null) {
                try {
                    columns.close();
                } catch (SQLException e) {
                    log.error("Error closing ResultSet: " + e.getMessage());
                }
            }
        }
        return tableJson;
    }

    /**
     * 获取数据库中所有表的结构信息
     *
     * @return 所有表结构信息的 JSON 数组
     * @throws SQLException 处理数据库操作时可能出现的异常
     */
    public JSONArray getAllTableStructures(DataSource dataSource) throws SQLException {
        if (dataSource == null) {
            log.error("数据源未成功创建，无法获取表结构信息。");
            return new JSONArray();
        }
        JSONArray allTablesArray = new JSONArray();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = null;
            try {
                if (this.tables != null && this.tables.length > 0) {
                    for (String tableName : this.tables) {
                        JSONObject tableStructure = getTableStructure(tableName, connection);
                        allTablesArray.add(tableStructure);
                    }
                } else {
                    tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
                    while (tables.next()) {
                        String tableName = tables.getString("TABLE_NAME");
                        if ((tablePrefix == null || tableName.startsWith(tablePrefix)) &&
                                (tableSuffix == null || tableName.endsWith(tableSuffix))) {
                            JSONObject tableStructure = getTableStructure(tableName, connection);
                            allTablesArray.add(tableStructure);
                        }
                    }
                }
            } finally {
                // 确保 ResultSet 关闭
                if (tables != null) {
                    try {
                        tables.close();
                    } catch (SQLException e) {
                        log.error("关闭ResultSet失败:{}", e.getMessage());
                    }
                }
            }
        }
        return allTablesArray;
    }
}