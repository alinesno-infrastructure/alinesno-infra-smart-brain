package com.alinesno.infra.smart.scene.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * ChatContentEditDto类
 */
@Data
public class ChatContentEditDto {

    @NotNull(message = "场景id不能为空")
    private Long sceneId ; // 所属场景

   @NotNull(message = "任务id不能为空")
    private Long taskId ;

    private List<String> chapters; // 章节id

    @Min(value = 1, message = "角色id不能为空")
    private long roleId ; // 角色id

}
