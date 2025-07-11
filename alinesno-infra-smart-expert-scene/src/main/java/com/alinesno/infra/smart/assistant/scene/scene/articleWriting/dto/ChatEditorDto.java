package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * ChatEditorDto
 */
@Data
public class ChatEditorDto {
    @NotNull(message = "场景ID不能为空")
    private Long sceneId; // 场景ID

    @NotNull(message = "文章ID不能为空")
    private Long articleId; // 文章ID

    @NotBlank(message = "文章内容不能为空")
    private String articleContent ;  // 文章内容

    @NotNull(message = "频道流ID不能为空")
    private Long channelStreamId; // 频道流ID

    @NotBlank(message = "修改的文本不能为空")
    private String modifyText ;  // 修改的文本

    @NotBlank(message = "消息内容不能为空")
    private String message; // 消息
}