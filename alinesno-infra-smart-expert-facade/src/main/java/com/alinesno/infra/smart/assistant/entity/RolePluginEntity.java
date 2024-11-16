package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName("role_plugins")
@TableComment("角色插件信息")
public class RolePluginEntity extends InfraBaseEntity {

    // 角色信息、插件信息
    @ColumnComment("角色ID")
    @Column(length = 50)
    @TableField(value = "role_id")
    private Long roleId;

    @ColumnComment("插件ID")
    @Column(length = 50)
    @TableField(value = "plugin_id")
    private Long pluginId;

}
