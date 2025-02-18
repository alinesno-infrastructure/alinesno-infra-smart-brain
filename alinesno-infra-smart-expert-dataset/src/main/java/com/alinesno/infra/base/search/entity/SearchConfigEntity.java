package com.alinesno.infra.base.search.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 检索服务自定义配置实体类
 * </p>
 *
 * @author [你的名字]
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("search_config")
@Table(comment = "检索服务自定义配置")
public class SearchConfigEntity extends InfraBaseEntity {

    /**
     * 启动配置
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 20)
    @ColumnComment("启动配置")
    private String resource;

    /**
     * 大模型地址
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("大模型地址")
    private String llmUrl;

    /**
     * API Key
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("API Key")
    private String apiKey;

    /**
     * 基础模型
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 20)
    @ColumnComment("基础模型")
    private String baseModel;

    /**
     * 向量模型类型
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 20)
    @ColumnComment("向量模型类型")
    private String vectorModelType;

    /**
     * 自定义向量模型地址
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("自定义向量模型地址")
    private String customVectorModelUrl;

    /**
     * Elasticsearch 索引库地址
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("Elasticsearch 索引库地址")
    private String esIndexUrl;

    /**
     * 索引库账号
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("索引库账号")
    private String indexDbUsername;

    /**
     * 索引库密码
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("索引库密码")
    private String indexDbPassword;

    /**
     * 向量库地址
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("向量库地址")
    private String vectorDbUrl;

    /**
     * 向量库类型
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 20)
    @ColumnComment("向量库类型")
    private String vectorDbType;

    /**
     * 向量库账号
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("向量库账号")
    private String vectorDbUsername;

    /**
     * 向量库密码
     */
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("向量库密码")
    private String vectorDbPassword;

}