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
 *
 * 向量数据集实体类
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "vector_dataset")
public class VectorDatasetEntity extends InfraBaseEntity {

    @ColumnComment("数据集图标")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 64)
    @TableField(value = "icon")
    private String icon;

    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 64)
    @ColumnComment("数据集名称")
    @TableField(value = "name")
    private String name;

    @ColumnComment("所有者ID")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @TableField(value = "owner_id")
    private long ownerId;

    @ColumnComment("数据集标识")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField
    private String collectionName;

    @ColumnComment("数据集状态")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 5)
    @TableField(value = "dataset_status")
    private String datasetStatus;

    @ColumnComment("是否为目录")
    @ColumnType(value = MySqlTypeConstant.SMALLINT, length = 1)
    @TableField(value = "is_directory")
    private Boolean isDirectory;

    @ColumnComment("数据集类型")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @TableField(value = "dataset_type")
    private long datasetType;

    @ColumnComment("标签")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "tags")
    private String tags;

    @ColumnComment("描述信息")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @TableField(value = "description")
    private String description;

    @ColumnComment("数据集大小")
    @ColumnType(value = MySqlTypeConstant.INT)
    @TableField(value = "dataset_size")
    private int datasetSize;

    @ColumnComment("访问权限")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @TableField(value = "access_permission")
    private String accessPermission;

    @ColumnComment("数据集版本")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 5)
    @TableField(value = "dataset_version")
    private String datasetVersion;

    @ColumnComment("数据集来源")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 256)
    @TableField(value = "dataset_source")
    private String datasetSource;

    @ColumnComment("数据集链接")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 256)
    @TableField(value = "dataset_link")
    private String datasetLink;

    @ColumnComment("预处理信息")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 256)
    @TableField(value = "preprocessing_info")
    private String preprocessingInfo;

    @ColumnComment("是否为记忆库(0知识库|1记忆库)")
    @ColumnType(value = MySqlTypeConstant.SMALLINT, length = 1)
    @TableField(value = "is_memory")
    private int isMemory = 0;

    @ColumnComment("搜索类型")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @TableField(value = "search_type")
    private String searchType;

    @ColumnComment("引用上限")
    @ColumnType(value = MySqlTypeConstant.INT)
    @TableField(value = "quote_limit")
    private int quoteLimit;

    @ColumnComment("是否重排")
    @ColumnType(value = MySqlTypeConstant.SMALLINT, length = 1)
    @TableField(value = "reorder_results")
    private int reorderResults;

    @ColumnComment("最低相关度")
    @ColumnType(value = MySqlTypeConstant.VARCHAR)
    @TableField(value = "min_relevance")
    private String minRelevance;

}
