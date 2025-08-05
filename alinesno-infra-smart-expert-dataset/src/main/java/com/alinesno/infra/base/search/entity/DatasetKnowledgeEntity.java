package com.alinesno.infra.base.search.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据集知识库
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "dataset_knowledge")
public class DatasetKnowledgeEntity extends InfraBaseEntity {

    @ColumnType(length = 64, value = MySqlTypeConstant.BIGINT)
    @ColumnComment("数据集Id")
    @TableField
    private Long datasetId ;

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("文档名称")
    @TableField
    private String documentName ; // 文档名称

    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("文档内容")
    @TableField
    private String documentContent ; // 文档内容

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 256)
    @ColumnComment("文档描述")
    @TableField
    private String documentDesc ; // 文档描述

    @ColumnType(MySqlTypeConstant.BIGINT)
    @ColumnComment("文档数量")
    @TableField
    private int documentCount ; // 文档数量

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("文档id（保存到云存储中的id号）")
    @TableField
    private String documentCloudId;

    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("向量数据id(以逗号做为分割)")
    @TableField
    private String vectorDataIds ;

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 256)
    @ColumnComment("文件本地存储路径")
    @TableField
    private String filePath;

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 10)
    @ColumnComment("是否上传，0: 未上传，1: 已上传")
    @TableField
    private String hasUploaded;

    @ColumnType(length = 64, value = MySqlTypeConstant.BIGINT)
    @ColumnComment("文件大小，单位字节")
    @TableField
    private String fileSize;

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("文件类型")
    @TableField
    private String fileType;

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("状态（未导入、导入中、导入完成、导入失败）")
    @TableField
    private String status;

    @ColumnType(length = 64, value = MySqlTypeConstant.BIGINT)
    @ColumnComment("理想分块")
    @TableField
    private String idealChunk;

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("自定义分割符")
    @TableField
    private String customSeparator;

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("处理方式（直接分段、增强处理、问答拆分）")
    @TableField
    private String processingMethod;

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("处理参数")
    @TableField
    private String processingParam ;

    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 2)
    @ColumnComment("是否自动导入(1自动导入|0手动导入)")
    @TableField
    private Integer hasAutoImport ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("Embedding模型ID")
    private Long embeddingModelId ;

    // 文档识别模型,比如pdf之类
    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("文档识别模型ID")
    private Long documentRecognitionModelId ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("OCR模型ID")
    private Long ocrModelId ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("Rerank模型ID")
    private Long rerankModelId ;

}