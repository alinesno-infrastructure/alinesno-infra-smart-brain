package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档审核场景信息实体类
 */
@EqualsAndHashCode(callSuper = true)
@TableName("document_review_scene")
@Data
public class DocReviewSceneEntity extends InfraBaseEntity {

    @TableField("scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private long sceneId;

    @TableField("document_id")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, comment = "文档ID")
    private String documentId;   // 文档ID

    @TableField("document_name")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 128, comment = "文档名称")
    private String documentName ;   // 文档名称

    @TableField("analysis_agent_engineer")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, comment = "分析智能工程师")
    private long analysisAgentEngineer; // 分析智能工程师

    @TableField("logic_reviewer_engineer")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, comment = "逻辑审核工程师")
    private long logicReviewerEngineer; // 逻辑审核工程师


//    // 审核清单生成方式
//    @TableField("review_list_option")
//    @Column(type = MySqlTypeConstant.VARCHAR, length = 16, comment = "审查清单生成方式")
//    private String reviewListOption ;

    @TableField("gen_status")
    @Column(type = MySqlTypeConstant.INT, length = 1, comment = "生成状态")
    private Integer genStatus = 0 ;

}