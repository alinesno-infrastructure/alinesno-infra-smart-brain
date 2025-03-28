package com.alinesno.infra.smart.scene.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

/**
 * ChatContentEditDto类
 */
@Data
public class ChatContentEditDto {

    @Min(value = 1, message = "场景id不能为空")
    private long sceneId ; // 所属场景

    private List<String> chapters; // 章节id

    @Min(value = 1, message = "角色id不能为空")
    private long roleId ; // 角色id

}
