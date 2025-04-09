package com.alinesno.infra.smart.assistant.scene.scene.documentReader.dto;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文档场景信息传输对象
 * 用于文档审核场景中传输相关信息和配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocReaderSceneInfoDto extends SceneInfoDto {

    private long sceneId;  // 场景ID
    private String documentId;   // 文档ID
    private String documentName; // 文档名称

    private long summaryAgentEngineer; // 分析智能工程师ID
    private IndustryRoleEntity summaryAgentEngineerEntity;  // 分析智能工程师实体

    private long caseQueryAgentEngineer; // 逻辑审核工程师ID
    private IndustryRoleEntity caseQueryAgentEngineerEntity;  // 逻辑审核工程师实体

}
