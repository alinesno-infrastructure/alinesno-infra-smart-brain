package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 考生分组DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamineeGroupDTO extends BaseDto {

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @NotBlank(message = "图标不能为空")
    private String icon;
}