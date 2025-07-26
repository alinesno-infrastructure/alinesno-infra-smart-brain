package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentFormatDTO {

    /**
     * 模板ID
     */
    @NotNull(message = "模板ID不能为空")
    private String templateId ;

    /**
     * 需要格式化的文档内容
     */
    @NotNull(message = "文档内容不能为空")
    private String content ;

}
