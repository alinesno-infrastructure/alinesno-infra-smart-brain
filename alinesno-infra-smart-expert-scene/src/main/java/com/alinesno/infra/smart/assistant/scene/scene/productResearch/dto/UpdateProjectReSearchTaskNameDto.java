package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改文档名称的DTO
 */
@Data
public class UpdateProjectReSearchTaskNameDto {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotBlank(message = "任务名称不能为空")
    private String taskName ;

}
