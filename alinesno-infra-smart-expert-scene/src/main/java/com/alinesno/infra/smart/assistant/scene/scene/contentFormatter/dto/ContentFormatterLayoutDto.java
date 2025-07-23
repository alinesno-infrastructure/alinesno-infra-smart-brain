package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容格式化布局配置DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "ContentFormatterLayoutDTO", description = "内容格式化布局配置DTO")
public class ContentFormatterLayoutDto extends BaseDto {

    @ApiModelProperty(value = "排版名称", required = true)
    private String name;

    @ApiModelProperty(value = "排版描述")
    private String layoutDesc;

    @ApiModelProperty(value = "排版分组ID")
    private Long groupId;

    @ApiModelProperty(value = "排版配置内容", required = true)
    private String layoutConfig;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}