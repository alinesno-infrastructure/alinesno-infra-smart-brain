package com.alinesno.infra.smart.scene.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LeaderPlanDto {

    @Min(value = 1, message = "screenId不能为空")
    private Long sceneId ; // 场景ID

    @NotBlank(message = "执行目标不能为空")
    private String message ; // 执行目标

}
