package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文档场景信息传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocSceneInfoDto extends SceneInfoDto {

    private long sceneId;
    private String documentId;   // 文档ID

    private long analysisAgentEngineer; // 分析智能工程师
    private IndustryRoleEntity analysisAgentEngineerEntity;

    private long logicReviewerEngineer; // 逻辑审核工程师
    private IndustryRoleEntity logicReviewerEngineerEntity;

    private String contractType;
    private String reviewPosition;
    private String reviewList;
    private String reviewListKnowledgeBase;
    private String contractOverview;

}
