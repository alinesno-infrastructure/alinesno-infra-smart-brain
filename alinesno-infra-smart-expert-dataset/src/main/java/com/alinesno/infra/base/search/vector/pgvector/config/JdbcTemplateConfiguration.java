package com.alinesno.infra.base.search.vector.pgvector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
public class JdbcTemplateConfiguration {

    @Value("${spring.datasource.dynamic.datasource.postgresql.url:}")
    private String jdbcUrl;

    @Value("${spring.datasource.dynamic.datasource.postgresql.username:}")
    private String username;

    @Value("${spring.datasource.dynamic.datasource.postgresql.password:}")
    private String password;

     // 获取JdbcTemplate Bean对象
    @Bean("pgvectorJdbcTemplate")
    public JdbcTemplate getJdbcTemplate() {
        DataSource dataSource = configureDataSource();
        return new JdbcTemplate(dataSource);
    }


    private DataSource configureDataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        try {
            // 加载PostgreSQL的JDBC驱动类
            Class.forName("org.postgresql.Driver");
            Driver driver = (Driver) Class.forName("org.postgresql.Driver").newInstance();
            dataSource.setDriver(driver);

            String urlWithTimeout = addTimeoutParams(jdbcUrl);
            dataSource.setUrl(urlWithTimeout);

            dataSource.setUsername(username);
            dataSource.setPassword(password);
            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize data source", e);
        }
    }

    // 辅助方法：在 JDBC URL 中添加超时参数
    private String addTimeoutParams(String originalUrl) {
        StringBuilder urlBuilder = new StringBuilder(originalUrl);
        if (!originalUrl.contains("?")) {
            urlBuilder.append("?");
        } else {
            urlBuilder.append("&");
        }
        // 设置连接超时时间为 15 秒（15000 毫秒）
        urlBuilder.append("connectTimeout=15000");
        urlBuilder.append("&");
        // 设置查询超时时间为 45 秒（45000 毫秒）
        urlBuilder.append("socketTimeout=45000");
        urlBuilder.append("&");

        // 启用 TCP 保活机制
        urlBuilder.append("tcpKeepAlive=true");
        urlBuilder.append("&");
        // 开启连接的保活机制
        urlBuilder.append("keepAlive=true");
        urlBuilder.append("&");
        // TCP 保活探测消息的最大重试次数
        urlBuilder.append("tcpKeepAliveCount=3");
        urlBuilder.append("&");
        // TCP 连接在开始发送保活探测消息之前的空闲时间（单位：秒）
        urlBuilder.append("tcpKeepAliveIdle=1800");
        urlBuilder.append("&");
        // 两次连续的保活探测消息之间的时间间隔（单位：秒）
        urlBuilder.append("tcpKeepAliveInterval=60");

        return urlBuilder.toString();
    }

}