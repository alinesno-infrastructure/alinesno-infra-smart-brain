package com.alinesno.infra.smart.assistant.screen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * ChatContentEditDto类
 */
@Data
public class ChatContentEditDto {

    @Min(value = 1, message = "场景id不能为空")
    private long screenId ; // 所属场景

    @NotNull(message = "章节列表不能为空")
    @Size(min = 1, message = "章节列表至少需要包含一个元素")
    private List<String> chapters; // 章节id

    @Min(value = 1, message = "角色id不能为空")
    private long roleId ; // 角色id

}
