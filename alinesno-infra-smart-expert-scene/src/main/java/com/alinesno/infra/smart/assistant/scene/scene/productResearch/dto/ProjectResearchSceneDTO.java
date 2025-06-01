package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 *  项目跟进场景数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectResearchSceneDTO extends SceneInfoDto {

    private long sceneId;  // 场景ID

    private String processCollectorEngineer ;  //  进度采集工程师
    private List<IndustryRoleDto>  processCollectorEngineerEntity;

    private String progressAnalyzerEngineer ;  //  项目情况分析工程师
    private List<IndustryRoleDto>  progressAnalyzerEngineerEntity;

    private Integer genStatus = 0 ; // 生成状态
    private String promptContent; // 提示内容

}
