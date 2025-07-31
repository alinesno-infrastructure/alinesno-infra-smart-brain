package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.scene.entity.ContentFormatterDocumentEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * 文档操作接口数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentFormatterDocumentDto extends BaseDto {

    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    @NotNull(message = "文档内容不能为空")
    private String documentContent ;

    @NotNull(message = "文档名称不能为空")
    private String documentName ;

}
