package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

/**
 * 内容格式化规则DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentFormatterRuleDto extends BaseDto {

    @NotBlank(message = "规则名称不能为空")
    @Size(max = 128, message = "规则名称长度不能超过128个字符")
    private String ruleName;

    @NotBlank(message = "风险级别不能为空")
    private String riskLevel;

    @NotBlank(message = "规则内容不能为空")
    private String ruleContent;

    @NotBlank(message = "审核立场不能为空")
    private String reviewPosition;

//    @NotBlank(message = "文档类型不能为空")
//    private String docType;

    @NotBlank(message = "适用范围不能为空")
    private String scope;

    @Size(max = 500, message = "规则描述长度不能超过500个字符")
    private String ruleDescription;

    @NotNull(message = "模板分组ID不能为空")
    private Long groupId ;
}