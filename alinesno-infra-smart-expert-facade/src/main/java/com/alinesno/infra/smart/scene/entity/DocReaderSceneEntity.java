package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文档审核场景信息实体类
 */
@EqualsAndHashCode(callSuper = true)
@TableName("document_reader_scene")
@Data
public class DocReaderSceneEntity extends InfraBaseEntity implements Serializable {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private long sceneId;

    @TableField
    @Column(name = "document_id", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "文档ID")
    private String documentId;   // 文档ID

    @TableField
    @Column(name = "document_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "文档名称")
    private String documentName ;   // 文档名称

    @TableField
    @Column(name = "summary_agent_engineer", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "分析智能工程师")
    private long summaryAgentEngineer; // 分析智能工程师

    @TableField
    @Column(name = "case_query_agent_engineer", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "逻辑审核工程师")
    private long caseQueryAgentEngineer; // 逻辑审核工程师

    // 新增字段：审查清单知识库
    @TableField
    @Column(name = "review_list_knowledge_base", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "审查清单知识库")
    private String reviewListKnowledgeBase;

}