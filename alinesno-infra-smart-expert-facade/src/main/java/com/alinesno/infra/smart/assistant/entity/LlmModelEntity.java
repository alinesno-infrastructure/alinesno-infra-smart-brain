package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.alinesno.infra.common.security.mapper.AESEncryptHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 该类表示具体的大模型，继承自 InfraBaseEntity，包含通用的基础信息。
 * 对应数据库中的 large_model 表，一个大模型对应一个提供商和一个接入配置。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "llm_model" , autoResultMap = true)
@Table(comment = "大模型信息表")
public class LlmModelEntity extends InfraBaseEntity {

    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("图标")
    @TableField("icon")
    private String icon;

    /**
     * 大模型的名称，该字段在数据库表中不能为空。
     */
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("大模型名称")
    @TableField("model_name")
    private String modelName;

    /**
     * 模型类型
     */
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("模型类型")
    @TableField("model_type")
    private String modelType;

    /**
     * 大模型的描述信息，该字段在数据库表中可以为空。
     */
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("大模型描述")
    @TableField("description")
    private String description;

    /**
     * 该大模型所属的提供商的主键，用于关联提供商信息。
     */
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("所属提供商名称")
    @TableField("provider_code")
    private String providerCode;

    /**
     * 接入大模型所需的 API 密钥，该字段在数据库表中不能为空。
     */
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("API 密钥")
    @TableField(value = "api_key" , typeHandler = AESEncryptHandler.class)
    private String apiKey;

    /**
     * 接入大模型的 API 密钥，该字段在数据库表中可以为空。
     */
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("SecretKey 密钥")
    @TableField(value = "secret_key" , typeHandler = AESEncryptHandler.class)
    private String secretKey;

    /**
     * 接入大模型的 API 地址，该字段在数据库表中不能为空。
     */
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("API 地址")
    @TableField(value = "api_url" , typeHandler = AESEncryptHandler.class)
    private String apiUrl;

    /**
     * 模型名称
     */
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("模型名称")
    @TableField("model")
    private String model;

    /**
     * 模型权限
     */
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("模型权限")
    @TableField("model_permission")
    private String modelPermission;

}