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
 * LiteFlow Rule脚本
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("role_chain_script")
public class RoleChainScriptEntity extends InfraBaseEntity {

    @ColumnComment(value = "应用程序名称")
    @Column(type = MySqlTypeConstant.VARCHAR , length = 64)
    @TableField("application_name")
    private String applicationName;

    @ColumnComment(value = "脚本ID")
    @Column(type = MySqlTypeConstant.VARCHAR , length = 64)
    @TableField("script_id")
    private String scriptId;

    @ColumnComment(value = "脚本名称")
    @Column(type = MySqlTypeConstant.VARCHAR , length = 64)
    @TableField("script_name")
    private String scriptName;

    @ColumnComment(value = "脚本数据")
    @Column(type = MySqlTypeConstant.TEXT)
    @TableField("script_data")
    private String scriptData;

    @ColumnComment(value = "脚本类型")
    @Column(type = MySqlTypeConstant.VARCHAR , length = 16)
    @TableField("script_type")
    private String scriptType;

    @ColumnComment(value = "脚本语言")
    @Column(type = MySqlTypeConstant.VARCHAR , length = 6)
    @TableField("script_language")
    private String scriptLanguage;

    @ColumnComment(value = "创建时间")
    @Column(type = MySqlTypeConstant.DATETIME)
    @TableField(value = "create_time")
    private Date createTime;

    @ColumnComment(value = "此脚本是否生效")
    @Column(type = MySqlTypeConstant.TINYINT)
    @TableField("enable")
    private String enable;
}
