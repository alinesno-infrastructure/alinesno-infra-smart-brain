package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 工作台数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomizeWorkbenchDTO extends BaseDto {

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "描述不能为空")
    private String description;

//    @NotNull(message = "类型不能为空")
//    @Positive(message = "类型必须为正整数")
//    private Integer type;
//
//    @NotNull(message = "选择的智能体ID列表不能为空")
//    private List<String> selectAgentId;
//
//    @NotNull(message = "选择的场景ID列表不能为空")
//    private List<String> selectSceneId;

}    