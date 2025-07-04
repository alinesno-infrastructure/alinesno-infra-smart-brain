package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_knowledge")
@Table(comment = "项目知识库信息")
public class ProjectKnowledgeEntity extends InfraBaseEntity {

    @TableField("project_id")
    @Column(name = "project_id", type = MySqlTypeConstant.BIGINT, comment = "项目id")
    private Long projectId;

    // 所属场景ID、所属分组ID、文档名称、文档内容、文档md5编写
    @TableField("scene_id")
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, comment = "所属场景ID")
    private Long sceneId;

    @TableField("group_id")
    @Column(name = "group_id", type = MySqlTypeConstant.BIGINT, comment = "所属分组ID")
    private Long groupId;

    @TableField("doc_name")
    @Column(name = "doc_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "文档名称")
    private String docName;

    @TableField("doc_content")
    @Column(name = "doc_content", type = MySqlTypeConstant.LONGTEXT, comment = "文档内容")
    private String docContent;

    @TableField("doc_md5")
    @Column(name = "doc_md5", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "文档md5编写")
    private String docMd5;

    // 是否已经向量化setVectorStatus
    @TableField("vector_status")
    @Column(name = "vector_status", type = MySqlTypeConstant.TINYINT, comment = "是否已经向量化")
    private Integer vectorStatus;

    @TableField("file_type")
    @Column(name = "file_type", type = MySqlTypeConstant.VARCHAR, length = 10, comment = "文件类型")
    private String fileType ;

}