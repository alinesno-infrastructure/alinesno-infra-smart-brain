package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改文档的DTO
 */
@Data
public class ModifyDocumentDto {

    @NotNull(message = "任务ID不能为空")
    private String taskId;

    @NotNull(message = "文档内容不能为空")
    private String htmlContent ;

}
