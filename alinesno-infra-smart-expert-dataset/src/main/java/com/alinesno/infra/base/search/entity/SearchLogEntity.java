package com.alinesno.infra.base.search.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 功能名：搜索日志
 * 数据表：search_logs
 * 表备注：
 *
 * @TableName 表名注解，用于指定数据库表名
 * @TableField 字段注解，用于指定数据库字段名
 *
 * @author luoxiaodong
 * @version 1.0.0
 */

@TableName("search_logs")
@Data
public class SearchLogEntity extends InfraBaseEntity {

    // 日志ID
    @ColumnType(MySqlTypeConstant.BIGINT)
    @ColumnComment("日志ID，主键")
    @TableField("log_id")
    private Integer logId;

    // 用户ID
    @ColumnType(MySqlTypeConstant.BIGINT)
    @ColumnComment("用户ID，外键关联users表")
    @TableField("user_id")
    private Integer userId;

    // 搜索查询
    @ColumnType(length = 255)
    @ColumnComment("搜索查询")
    @TableField("search_query")
    private String searchQuery;

    // 响应时间
    @ColumnType(length = 11)
    @ColumnComment("响应时间（毫秒）")
    @TableField("response_time")
    private Integer responseTime;

    // 记录时间
    @ColumnType(length = 19)
    @ColumnComment("记录时间")
    @TableField("created_at")
    private Timestamp createdAt;
}