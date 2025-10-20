package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档审核规则组数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectKnowledgeGroupDto extends BaseDto {

    /**
     * 分组类型
     */
    @NotBlank(message = "分组类型不能为空")
    private String groupType ;

    /**
     * 规则组名称
     */
    @NotBlank(message = "规则组名称不能为空")
    private String groupName ;

    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空")
    private String description ;

    /**
     * 向量模型ID
     */
    @NotNull(message = "向量模型ID不能为空")
    private Long embeddingModelId;

    /**
     * OCR模型ID
     */
    private Long ocrModelId;

    /**
     * 文档识别模型ID
     */
    private Long documentRecognitionModelId;

    /**
     * 文档数量
     */
    private long documentCount = 0 ;
}
