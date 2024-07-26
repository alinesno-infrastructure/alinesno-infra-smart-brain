package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色插件配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("role_plugin")
public class RolePluginEntity extends InfraBaseEntity {

    private String name ; // 名称
    private String desc ; // 插件描述
    private String jarName ; // jar包名称
    private String author ; // 作者
    private String version ; // 版本号
    private String localPath; // 本地路径

}
