package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import lombok.Data;

/**
 * 文档审核规则的DTO类
 * 用于文档审核场景中，定义了需要审核的文档所依据的规则信息
 */
@Data
public class DocReviewRulesDto {

    /**
     * 规则ID
     */
    private long id ;

    /**
     * 规则名称
     * 用于标识审核规则的名称，便于理解和引用
     */
    private String ruleName;

    /**
     * 规则级别
     * 定义了审核规则的级别，可以用来区分规则的重要程度或适用范围
     */
    private String ruleLevel;

    /**
     * 规则内容
     * 描述了审核规则的具体内容，是执行审核时的依据
     */
    private String ruleContent;

    /**
     * 合同类型ID
     * 关联了合同类型，使得审核规则能够针对不同类型的合同进行定制
     */
    private String contractTypeId;
}
