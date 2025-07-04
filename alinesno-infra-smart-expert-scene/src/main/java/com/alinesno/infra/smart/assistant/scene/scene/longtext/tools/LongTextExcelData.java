/**
 * 用于文章写作场景中处理Excel数据的类
 * 该类封装了与Excel表格中文章相关的一系列数据和配置信息
 */
package com.alinesno.infra.smart.assistant.scene.scene.longtext.tools;

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
public class LongTextExcelData {
    /**
     * 文章的索引号
     * 用于唯一标识Excel表格中的一篇文章
     */
    private Integer index;

    /**
     * 文章的图标
     */
    private String icon ;

    /**
     * 文章的类型ID
     */
    private Long typeId ;
    
    /**
     * 文章的类型代码
     * 用于标识文章的类别，便于后续处理和统计
     */
    private String type;
    
    /**
     * 文章类型的名称
     * 提供文章类型的可读名称，便于理解文章属于哪一类
     */
    private String typeName;

    /**
     * 系统提示信息
     * 用于给文章写作提供系统级别的提示，如API调用限制、数据来源等
     */
    private String systemPrompt ;
    
    /**
     * 文章的提示信息
     * 用于给文章写作提供灵感或指导
     */
    private String prompt;
    
    /**
     * 文章的示例内容
     * 提供一个文章示例，帮助用户理解期望的文章格式或内容
     */
    private String demo;
    
    /**
     * 文章的标题
     * 概括文章内容的主题，是文章的重要标识信息
     */
    private String title;
    
    /**
     * 文章的描述
     * 对文章内容的简短介绍，帮助用户快速了解文章主旨
     */
    private String desc;
    
    /**
     * 结果配置信息
     * 用于指定文章生成或处理的结果应该如何配置，可能包含输出格式、保存位置等信息
     */
    private String resultConfig;
    
    /**
     * 配置信息
     * 可能包含文章处理的一些特定配置，如样式、模板等
     */
    private String config;
    
    /**
     * 权限信息
     * 标识文章的访问或修改权限，控制谁可以查看或编辑文章
     */
    private String power;

    /**
     * 失败原因
     */
    private Map<String, String> failReason ;
}
