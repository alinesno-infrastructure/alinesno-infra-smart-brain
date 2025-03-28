package com.alinesno.infra.smart.scene.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * LeaderUpdateDto类
 */
@Data
public class LeaderUpdateDto {

    private String leaderId ;

    @Min(value = 1, message = "场景ID不能为空")
    private Long sceneId ;

    @NotBlank(message = "角色类型不能为空")
    private String type ;

    private List<String> workerIds ;

}
