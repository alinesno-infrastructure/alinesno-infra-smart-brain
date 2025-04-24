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

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    protected DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @SneakyThrows
    @Override
    public String toJson() {
        ToolInfo toolInfo = getClass().getAnnotation(ToolInfo.class);
        String toolName = toolInfo.name().isEmpty() ? getClass().getSimpleName() : toolInfo.name();

        JSONObject toolJson = new JSONObject();
        toolJson.put("name", toolName);
        toolJson.put("description", toolInfo.description());
        toolJson.put("table Structures", getAllTableStructures(getDataSource()));

        List<JSONObject> paramDescriptions = getJsonObjects();

        toolJson.put("paramDescription", paramDescriptions);
        return JSON.toJSONString(toolJson);
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