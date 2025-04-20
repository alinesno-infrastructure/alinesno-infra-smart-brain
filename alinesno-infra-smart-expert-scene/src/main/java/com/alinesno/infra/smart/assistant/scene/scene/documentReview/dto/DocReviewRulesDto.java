package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档审核规则的DTO类
 * 用于文档审核场景中，定义了需要审核的文档所依据的规则信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocReviewRulesDto extends BaseDto {

    /**
     * 规则名称
     * 用于标识审核规则的名称，便于理解和引用
     */
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    /**
     * 风险级别
     */
    @NotBlank(message = "风险级别不能为空")
    private String riskLevel;

    /**
     * 规则内容
     * 描述了审核规则的具体内容，是执行审核时的依据
     */
    @NotBlank(message = "规则内容不能为空")
    private String ruleContent;

    /**
     * 关联的分组ID
     */
    @NotNull(message = "关联的分组ID不能为空")
    private Long groupId ;

    /**
     * 审核立场
     */
    @NotBlank(message = "审核立场不能为空")
    private String reviewPosition ;

    /**
     * 审核结果数量
     */
    private int auditResultCount ;
}
