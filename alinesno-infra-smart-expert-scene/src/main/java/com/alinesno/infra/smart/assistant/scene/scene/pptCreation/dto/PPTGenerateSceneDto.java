package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 深度搜索场景实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PPTGenerateSceneDto extends SceneInfoDto {

    private long sceneId;  // 场景ID

    private Long pptPlannerEngineer ;
    private List<IndustryRoleDto> pptPlannerEngineerEntity ;  // PPT内容规划专员ID

    private Long pptGeneratorEngineer ;
    private List<IndustryRoleDto> pptGeneratorEngineerEntity ;  // PPT内容生成专员ID

    private Integer genStatus = 0 ; // 生成状态
    private String promptContent; // 提示内容

}
