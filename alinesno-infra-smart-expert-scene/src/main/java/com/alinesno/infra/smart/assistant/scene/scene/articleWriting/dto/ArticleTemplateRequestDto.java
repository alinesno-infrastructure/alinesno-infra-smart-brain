package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新增或者更新模板请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleTemplateRequestDto extends BaseDto {

    @NotBlank(message = "原始文件名不能为空")
    private String originalFilename;

    @NotBlank(message = "模板引擎不能为空")
    private String templateEngine ;

    // 文件类型
    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    // 模板图片
    @NotNull(message = "模板图片ID不能为空")
    private Long templateImage;

    // 模板名称
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    // 模板数据范围
    @NotBlank(message = "模板数据范围不能为空")
    private String templateDataScope ;

    // 文件存储ID
    @NotBlank(message = "文件存储ID不能为空")
    private String storageFileId;

    // 模板类型
    @NotBlank(message = "模板类型不能为空")
    private String templateType;

}