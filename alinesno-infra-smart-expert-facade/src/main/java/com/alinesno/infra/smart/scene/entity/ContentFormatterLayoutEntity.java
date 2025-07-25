package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容格式化布局配置，针对于不同场景的内容格式化
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("content_formatter_layout")
public class ContentFormatterLayoutEntity extends InfraBaseEntity {

    // 排版图标
    @TableField
    @Column(comment = "排版图标" , length = 32, type = MySqlTypeConstant.VARCHAR)
    private String icon;

    @TableField
    @Column(comment = "排版名称" , length = 50 , type = MySqlTypeConstant.VARCHAR)
    private String name;

    @TableField
    @Column(comment = "排版描述" , length = 256 , type = MySqlTypeConstant.VARCHAR)
    private String layoutDesc;

    @TableField
    @Column(comment = "排版分组" , length = 32, type = MySqlTypeConstant.BIGINT)
    private Long groupId;

    @TableField
    @Column(comment = "排版配置" , type = MySqlTypeConstant.TEXT)
    private String layoutConfig;

    @TableField
    @Column(comment = "排版排序" , length = 11 , type = MySqlTypeConstant.INT)
    private Integer sort;

}