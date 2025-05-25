package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

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
public class ExamPagerSceneDto extends SceneInfoDto {

    private long sceneId;  // 场景ID

    private Long questionGeneratorEngineer ;
    private List<IndustryRoleDto> questionGeneratorEngineerEntity ;  // 分析智能工程师实体

    private Long answerCheckerEngineer ;
    private List<IndustryRoleDto> answerCheckerEngineerEntity ;  // 分析智能工程师实体

    private Long paperGeneratorEngineer ;
    private List<IndustryRoleDto> paperGeneratorEngineerEntity ;  // 分析智能工程师实体

    private Integer genStatus = 0 ; // 生成状态
    private String promptContent; // 提示内容

}
