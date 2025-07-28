package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
     * 规则组所有列表
     */
    private List<ProjectKnowledgeDto> rules;

}
