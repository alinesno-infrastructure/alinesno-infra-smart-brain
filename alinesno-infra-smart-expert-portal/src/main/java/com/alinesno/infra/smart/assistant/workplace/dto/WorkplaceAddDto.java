package com.alinesno.infra.smart.assistant.workplace.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作台添加DTO类
 * 继承自BaseDto，用于处理工作台添加的相关数据传输
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkplaceAddDto extends BaseDto {

    // 工作台名称，不能为空
    @NotBlank(message = "工作台名称不能为空")
    private String name;

    // 工作台描述，不能为空
    @NotBlank(message = "工作台描述不能为空")
    private String description;

    // 工作台背景图，不能为空
    @NotBlank(message = "工作台背景图不能为空")
    private String backgroundImage;

    // 工作台类型，不能为空
    @NotBlank(message = "工作台类型不能为空")
    private String workplaceType ;

    // 工作台来源
    private String workplaceSource ;

    // 来源工作台ID
    private long sourceWorkplaceId ;

}
