package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档重写规则配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("content_formatter_rewrite")
public class ContentFormatterRewriteEntity extends InfraBaseEntity {

    // 重写的规则名称、规则描述、规则配置（长文本）、排序
    @TableField
    @Column(comment = "名称" , length = 50 , type = MySqlTypeConstant.VARCHAR)
    private String name ;

    @TableField
    @Column(comment = "描述" , length = 256 , type = MySqlTypeConstant.VARCHAR)
    private String rewriteDesc ;

    @TableField
    @Column(comment = "配置" , type = MySqlTypeConstant.TEXT)
    private String rewriteConfig ;

    @TableField
    @Column(comment = "排序" , length = 11 , type = MySqlTypeConstant.INT)
    private Integer sort ;

}