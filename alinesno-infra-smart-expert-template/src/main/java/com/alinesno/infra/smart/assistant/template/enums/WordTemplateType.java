package com.alinesno.infra.smart.assistant.template.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum WordTemplateType {

    RESUME_TEMPLATE(32000L, "resume_template", "简历模板，用于个人简历的制作，帮助求职者展示个人信息、工作经历、教育背景等内容。", "简历模板"),
    CONTRACT_AGREEMENT(21000L, "contract_agreement", "合同协议模板，涵盖各种商业或个人合同协议，如租赁合同、销售合同等，为合同的起草提供规范格式。", "合同协议模板"),
    ENTERPRISE_MANAGEMENT(19000L, "enterprise_management", "企业管理模板，可用于企业的各类管理文档，如管理制度、流程规范等，助力企业高效运营。", "企业管理模板"),
    ELECTRONIC_NEWSPAPER(13000L, "electronic_newspaper", "电子小报模板，用于制作电子形式的小报，可用于校园、社区等信息传播。", "电子小报模板"),
    PROJECT_MANAGEMENT(13000L, "project_management", "项目管理模板，包含项目计划、进度跟踪、风险管理等文档，有助于项目的顺利推进。", "项目管理模板"),
    EDUCATION_TEACHING(9091L, "education_teaching", "教育教学模板，适用于教师备课、教学方案设计、学生成绩管理等教育教学场景。", "教育教学模板"),
    FINANCIAL_TAX(890L, "financial_tax", "财务税务模板，用于财务报表制作、税务申报等财务税务工作。", "财务税务模板");

    private final Long id;
    private final String code;
    private final String desc;
    private final String name;

    /**
     * 获取到所有模板类型返回 List<Map<String , Object>>
     */
    public static List<Map<String, Object>> getAllTemplateType() {
        return Arrays.stream(WordTemplateType.values())
                .map(type -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", type.getId());
                    map.put("code", type.getCode());
                    map.put("desc", type.getDesc());
                    map.put("name", type.getName());
                    return map;
                })
                .collect(Collectors.toList());
    }

}