package com.alinesno.infra.smart.assistant.scene.scene.prototypeDesign.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * PrototypeChapterDto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PrototypeChapterDto extends SceneInfoDto {

    private long sceneId;  // 场景ID

    private Long requirementAnalyzer ;
    private List<IndustryRoleDto> requirementAnalyzerEntity ;  // 文章内容规划专员ID

    private Long prototypeDesigner ;
    private List<IndustryRoleDto> prototypeDesignerEntity ;  // 文章内容生成专员ID

    private Integer genStatus = 0 ; // 生成状态
    private String promptContent; // 提示内容

}
