//package com.alinesno.infra.smart.brain.entity;
//
//import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
//import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
//import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.ToString;
//
//import java.util.Date;
//
///**
// *
// * 向量数据集实体类
// */
//@ToString
//@EqualsAndHashCode(callSuper = true)
//@Data
//@TableName(value = "vector_dataset")
//public class VectorDatasetEntity extends InfraBaseEntity {
//
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @ColumnComment("数据来源类型")
//    @TableField(value = "name")
//    private String name;
//
//    @ColumnComment("所有者ID")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "owner_id")
//    private Integer ownerId;
//
//    @ColumnComment("导入时间")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "import_time")
//    private Date importTime;
//
//    @ColumnComment("数据问题描述")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "data_issues")
//    private String dataIssues;
//
//    @ColumnComment("数据集状态")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "dataset_status")
//    private String datasetStatus;
//
//    @ColumnComment("是否为目录")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "is_directory")
//    private Boolean isDirectory;
//
//    @ColumnComment("数据集类型")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "dataset_type")
//    private String datasetType;
//
//    @ColumnComment("标签")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "tags")
//    private String tags;
//
//    @ColumnComment("描述信息")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "description")
//    private String description;
//
//    @ColumnComment("数据集大小")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "dataset_size")
//    private String datasetSize;
//
//    @ColumnComment("数据集路径")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "dataset_path")
//    private String datasetPath;
//
//    @ColumnComment("访问权限")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "access_permission")
//    private String accessPermission;
//
//    @ColumnComment("数据集版本")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "dataset_version")
//    private String datasetVersion;
//
//    @ColumnComment("数据集来源")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "dataset_source")
//    private String datasetSource;
//
//    @ColumnComment("数据集链接")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "dataset_link")
//    private String datasetLink;
//
//    @ColumnComment("预处理信息")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "preprocessing_info")
//    private String preprocessingInfo;
//
//    @ColumnComment("向量数据")
//    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
//    @TableField(value = "vectors")
//    private byte[] vectors;
//}
