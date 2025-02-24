package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName("tool_type")
@TableComment("工具类型信息")
public class ToolTypeEntity extends InfraBaseEntity {

    // 工具图标
    @ColumnComment("工具图标")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @TableField(value = "icon")
    private String icon;

    @ColumnComment("工具名称")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "name")
    private String name;

}
