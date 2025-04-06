package com.alinesno.infra.smart.assistant.scene.core.entity;

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
@TableName("document_review_scene")
@Data
public class DocReviewSceneEntity extends InfraBaseEntity implements Serializable {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 20, comment = "场景ID")
    private long sceneId;

    @TableField
    @Column(name = "document_id", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "文档ID")
    private String documentId;   // 文档ID

    @TableField
    @Column(name = "analysis_agent_engineer", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "分析智能工程师")
    private long analysisAgentEngineer; // 分析智能工程师

    @TableField
    @Column(name = "logic_reviewer_engineer", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "逻辑审核工程师")
    private long logicReviewerEngineer; // 逻辑审核工程师

    // 新增字段：合同类型
    @TableField
    @Column(name = "contract_type", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "合同类型")
    private String contractType;

    // 新增字段：审核立场
    @TableField
    @Column(name = "review_position", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "审核立场")
    private String reviewPosition;

    // 新增字段：审查清单
    @TableField
    @Column(name = "review_list", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "审查清单")
    private String reviewList;

    // 新增字段：审查清单知识库
    @TableField
    @Column(name = "review_list_knowledge_base", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "审查清单知识库")
    private String reviewListKnowledgeBase;

    // 新增字段：合同概览
    @TableField
    @Column(name = "contract_overview", type = MySqlTypeConstant.TEXT, comment = "合同概览")
    private String contractOverview;
}