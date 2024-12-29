package com.alinesno.infra.smart.assistant.screen.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景实体类,针对于具体的业务场景配置管理，根据要求自动出题<br/>
 * 1. 题目出来之后，进行答题，答完之后自动评分，并记录分数。
 * 2. 分析出结果情况，并汇总
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamScreenDto extends BaseDto {

    /**
     * 出题人员：负责根据考试大纲和要求编写题目，确保试题的质量和难度适中。
     */
    private List<IndustryRoleDto> examEditors = new ArrayList<>();

    /**
     * 成绩统计与分析人员：负责收集、整理和分析考试数据，计算成绩，并进行必要的统计分析。
     */
    private IndustryRoleDto examAnalysis ;

}

