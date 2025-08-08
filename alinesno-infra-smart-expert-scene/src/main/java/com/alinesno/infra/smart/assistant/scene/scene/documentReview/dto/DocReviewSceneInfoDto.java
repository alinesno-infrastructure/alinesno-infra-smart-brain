package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 文档场景信息传输对象
 * 用于文档审核场景中传输相关信息和配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocReviewSceneInfoDto extends SceneInfoDto {

    private long sceneId;  // 场景ID
    private String documentId;   // 文档ID
    private String documentName; // 文档名称

    private long analysisAgentEngineer; // 分析智能工程师ID
    private IndustryRoleEntity analysisAgentEngineerEntity;  // 分析智能工程师实体

    private long logicReviewerEngineer; // 逻辑审核工程师ID
    private IndustryRoleEntity logicReviewerEngineerEntity;  // 逻辑审核工程师实体

    private String contractType;  // 合同类型
    private String reviewPosition;  // 审查立场
    private String reviewListOption ;  // 审核列表选项
    private Long auditId ; // 审核ID
    private String reviewList;  // 审核列表
    private List<DocReviewRulesDto> reviewListDtos ;  // 审核列表
    private String reviewListKnowledgeBase;  // 审核列表知识库
    private String contractOverview;  // 合同概述
    private Integer genStatus ; // 生成状态

    private String reviewGenStatus; // 审核清单生成状态
    private String resultGenStatus ; // 审核结果生成状态
    private String documentParseStatus ; // 文档内容解析状态

    private List<Map<String , String>> contractTypes ;   // 合同类型列表，每项包含合同类型的有关信息
}
