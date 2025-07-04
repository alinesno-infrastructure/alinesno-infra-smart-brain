package com.alinesno.infra.smart.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述：账户主页
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("account_home_page")
public class AccountHomePageEntity extends InfraBaseEntity {

    @TableField
    @Column(comment = "主页路径" , length = 255 , isNull = false)
    private String homePage;   // 主页路径

    @TableField
    @Column(comment = "类型" , length = 10, isNull = false)
    private String type;       // 类型: default/settings
}