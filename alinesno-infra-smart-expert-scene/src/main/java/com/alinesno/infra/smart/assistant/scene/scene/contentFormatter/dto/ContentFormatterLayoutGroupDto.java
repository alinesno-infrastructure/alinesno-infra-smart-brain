package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容格式化规则DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentFormatterLayoutGroupDto extends BaseDto {

    @NotBlank(message = "分组名称不能为空")
    private String name;

    @NotNull(message = "分组名称不能为空")
    private String groupType ; // 分组类型

    @NotNull(message = "分组图标不能为空")
    private String icon;

    @NotNull(message = "排序不能为空")
    private Integer sort;

    @NotNull(message = "数据范围不能为空")
    private String dataScope ;
}