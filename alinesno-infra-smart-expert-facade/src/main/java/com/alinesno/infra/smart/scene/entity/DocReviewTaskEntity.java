package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文档审核任务
 */
@EqualsAndHashCode(callSuper = true)
@TableName("document_review_task")
@Data
public class DocReviewTaskEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "task_name", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "任务名称")
    private String taskName;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "document_review_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "文档审核场景ID")
    private Long documentReviewSceneId;

    @TableField
    @Column(name = "task_description", type = MySqlTypeConstant.LONGTEXT, comment = "任务描述")
    private String taskDescription;

    @TableField
    @Column(name = "task_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "任务状态")
    private String taskStatus;

    @TableField
    @Column(name = "task_start_time", type = MySqlTypeConstant.DATETIME, comment = "任务开始时间")
    private Date taskStartTime;

    @TableField
    @Column(name = "task_end_time", type = MySqlTypeConstant.DATETIME, comment = "任务结束时间")
    private Date taskEndTime;

    @TableField
    @Column(name = "document_id", type = MySqlTypeConstant.VARCHAR, comment = "审核文档ID")
    private String documentId;

    @TableField
    @Column(name = "document_name", type = MySqlTypeConstant.VARCHAR, comment = "审核文档名称")
    private String documentName;

    @TableField
    @Column(name = "gen_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成状态(1已生成|0未生成)")
    private Integer genStatus = 0;

    // 审核清单生成方式
    @TableField("review_list_option")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 16, comment = "审查清单生成方式")
    private String reviewListOption ;

    @TableField("audit_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "审查清单ID")
    private Long auditId ;

    // 新增字段：审查清单
    @TableField("review_list")
    @Column(type = MySqlTypeConstant.LONGTEXT, comment = "审查清单")
    private String reviewList;

    // 新增字段：审查清单知识库
    @TableField("review_list_knowledge_base")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 256, comment = "审查清单知识库")
    private String reviewListKnowledgeBase;

    // 审核结果
    @TableField("review_result")
    @Column(type = MySqlTypeConstant.LONGTEXT , comment = "审核结果")
    private String reviewResult;

    // 新增字段：合同概览
    @TableField("contract_overview")
    @Column(type = MySqlTypeConstant.TEXT, comment = "合同概览")
    private String contractOverview;


    // 新增字段：合同类型
    @TableField("contract_type")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, comment = "合同类型")
    private String contractType;

    // 新增字段：审核立场
    @TableField("review_position")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 2, comment = "审核立场")
    private String reviewPosition;

}