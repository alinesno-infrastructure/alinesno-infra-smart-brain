package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型管理实体类(ChatGPT/文心一言/科大讯飞/llama2)
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("prompt_model")
public class PromptModelEntity extends InfraBaseEntity {

    // fields

    /**
     * 模型名称
     */
    @Excel(name="模型名称")
    @TableField("model_name")
    private String modelName;

    /**
     * 行业分类
     */
    @Excel(name="行业分类")
    @TableField("industry_category")
    private String industryCategory;

    /**
     * 业务分类
     */
    @Excel(name="业务分类")
    @TableField("business_category")
    private String businessCategory;

    /**
     * 资源名称
     */
    @Excel(name="资源名称")
    @TableField("resource_name")
    private String resourceName;

    /**
     * 资源摘要
     */
    @Excel(name="资源摘要")
    @TableField("resource_summary")
    private String resourceSummary;

    /**
     * 模型描述
     */
    @Excel(name="模型描述")
    @TableField("model_description")
    private String modelDescription;

    /**
     * 模型字段
     */
    @Excel(name="模型字段")
    @TableField("model_fields")
    private String modelFields;

    /**
     * 模型来源
     */
    @Excel(name="模型来源")
    @TableField("model_source")
    private String modelSource;

    /**
     * 模型标签
     */
    @Excel(name="模型标签")
    @TableField("model_tags")
    private String modelTags;

}