package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 应用统计实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("app_statistics")
public class AppStatisticsEntity extends InfraBaseEntity {

    // fields

    /**
     * 来源系统
     */
    @Excel(name="来源系统")
    @TableField("source_system")
    private String sourceSystem;

    /**
     * 数据资产
     */
    @Excel(name="数据资产")
    @TableField("data_asset")
    private String dataAsset;

    /**
     * 运行天数
     */
    @Excel(name="运行天数")
    @TableField("running_days")
    private Long runningDays;

    /**
     * 数据存储量
     */
    @Excel(name="数据存储量")
    @TableField("data_storage")
    private BigDecimal dataStorage;

    /**
     * 数据请求量
     */
    @Excel(name="数据请求量")
    @TableField("data_requests")
    private Long dataRequests;

    /**
     * 数据主题数
     */
    @Excel(name="数据主题数")
    @TableField("data_subjects")
    private Long dataSubjects;

    /**
     * 资产行业分类数
     */
    @Excel(name="资产行业分类数")
    @TableField("asset_industry_categories")
    private Long assetIndustryCategories;

    /**
     * 资产主题数
     */
    @Excel(name="资产主题数")
    @TableField("asset_subjects")
    private Long assetSubjects;

    /**
     * 资产业务分类数
     */
    @Excel(name="资产业务分类数")
    @TableField("asset_business_categories")
    private Long assetBusinessCategories;

    /**
     * 资产专题数
     */
    @Excel(name="资产专题数")
    @TableField("asset_special_topics")
    private Long assetSpecialTopics;

}
