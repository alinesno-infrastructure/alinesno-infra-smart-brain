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
@TableName("account_role_plugin")
public class AccountRolePluginEntity extends InfraBaseEntity {

    private long accountId ; // 名称
    private long pluginId ; // 插件描述
    private int installStatus ; // 安装状态(1安装|0未安装|9安装失败)

}
