package com.alinesno.infra.smart.assistant.workplace.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * WorkplaceItemDto类用于表示工作场所项目的数据传输对象
 * 它继承自BaseDto类，并使用Lombok库来简化getter和setter的编写
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkplaceItemDto extends BaseDto {

    /**
     * 工作场所的ID，用于关联工作场所
     */
    @NotNull(message = "工作场所的ID不能为空")
    @Min(value = 1 , message = "工作场所的ID必须大于等于1")
    private Long workplaceId;


    /**
     * 项目的ID，用于关联具体的项目
     */
    @NotNull(message = "项目ID列表不能为空")
    private List<@NotBlank(message = "项目ID不能为空") String> itemIds;

    /**
     * 项目的类型，用于区分不同类型的项目
     */
    @NotBlank(message = "项目类型不能为空")
    private String itemType;

    /**
     * 排序顺序，默认为0，用于对项目进行排序
     */
    private Integer sortOrder = 0 ;
}
