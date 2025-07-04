package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一个知识库分组，一个向量数据集
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_knowledge_group")
@Table(comment = "知识库分组类型")
public class ProjectKnowledgeGroupEntity extends InfraBaseEntity {

    // 所属场景ID
    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "所属场景ID")
    private Long sceneId;

    // 分组类型(文档库/数据库)
    @TableField
    @Column(name = "group_type", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "分组类型(文档库document|数据库database)")
    private String groupType;

    // 分组名称、分组排序
    @TableField
    @Column(name = "group_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "分组名称")
    private String groupName;

    @TableField
    @Column(name = "group_sort", type = MySqlTypeConstant.INT, length = 5, comment = "分组排序")
    private Integer groupSort;

    // 向量数据集名称(唯一的)
    @TableField
    @Column(name = "vector_dataset_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "向量数据集名称")
    private String vectorDatasetName;

}