package com.alinesno.infra.smart.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

// Agent商店实体类
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("agent_store")
public class AgentStoreEntity extends InfraBaseEntity {

    @TableField("agent_id")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "Agent ID")
    private Long agentId;

    @TableField("sort_order")
    @Column(type = MySqlTypeConstant.INT, isNull = false, comment = "排序")
    private Integer sortOrder;

    @TableField("agent_type")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "Agent类型")
    private Long agentType;

    // 添加角色所属的组织（只为当前组织可以看到)
    @TableField("role_organization_id")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "角色所属组织")
    private Long roleOrganizationId;

}