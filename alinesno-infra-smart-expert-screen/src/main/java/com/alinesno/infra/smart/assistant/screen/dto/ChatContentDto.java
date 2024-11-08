package com.alinesno.infra.smart.assistant.screen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新章节内容
 */
@Data
public class ChatContentDto {

    @Min(value = 1, message = "id不能为空")
    private long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

}
