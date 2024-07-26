package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * LiteFlow Chain表信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("role_chain")
public class RoleChainEntity extends InfraBaseEntity {

    @ColumnComment(value = "应用程序名称")
    @Column(type = MySqlTypeConstant.VARCHAR , length = 64)
    @TableField("chain_application_name")
    private String chainApplicationName;

    @Column(type = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("链名称")
    @TableField
    private String chainName;

    @Column(type = MySqlTypeConstant.VARCHAR , length = 128)
    @ColumnComment("链描述")
    @TableField
    private String chainDescription;

    @Column(type = MySqlTypeConstant.TEXT)
    @ColumnComment("el数据")
    @TableField
    private String elData;

    @ColumnComment(value = "创建时间")
    @Column(type = MySqlTypeConstant.DATETIME)
    @TableField("create_time")
    private Date createTime;

    @ColumnComment(value = "此chain是否生效")
    @Column(type = MySqlTypeConstant.TINYINT)
    @TableField("enable")
    private String enable;
}
