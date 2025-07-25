/**
 * 用于文章写作场景中处理Excel数据的类
 * 该类封装了与Excel表格中文章相关的一系列数据和配置信息
 */
package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Excel数据模型类
 * 用于存储文章写作场景中从Excel表格读取的数据
 * 包括文章的索引、类型、提示、示例、标题、描述、结果配置、配置和权限等信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentRuleExcelData {

    private String ruleName;
    private String riskLevel;
    private String ruleContent;
    private Long groupId;
    private String reviewPosition;
    private String docType;
    private String scope;
    private String ruleDescription;

    /**
     * 失败原因
     */
    private Map<String, String> failReason ;
}
