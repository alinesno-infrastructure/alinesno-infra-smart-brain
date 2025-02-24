//package com.alinesno.infra.smart.assistant.entity;
//
//import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
//import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
//import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.Entity;
//
//@EqualsAndHashCode(callSuper = true)
//@Data
//@Entity
//@NoArgsConstructor
//@TableName("role_tool")
//@TableComment("角色工具信息")
//public class RoleToolEntity extends InfraBaseEntity {
//
//    // 角色信息、插件信息
//    @ColumnComment("角色ID")
//    @Column(length = 50)
//    @TableField(value = "role_id")
//    private Long roleId;
//
//    @ColumnComment("插件ID")
//    @Column(length = 50)
//    @TableField(value = "plugin_id")
//    private Long toolId;
//
//    public RoleToolEntity(Long roleId, Long toolId) {
//        this.roleId = roleId;
//        this.toolId = toolId;
//    }
//}
