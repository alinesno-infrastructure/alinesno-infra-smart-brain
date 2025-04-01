package com.alinesno.infra.smart.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Agent所属的场景
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("agent_scene")
public class AgentSceneEntity extends AgentStoreEntity {

    // 场景的唯一标识符
    @TableField("scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "场景的唯一标识符")
    private long sceneId;

    // 场景的代码标识，用于内部引用和识别
    @TableField("scene_code")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, isNull = false, comment = "场景的代码标识，用于内部引用和识别")
    private String sceneCode;

    // 代理智能体的唯一标识符
    @TableField("agent_type_id")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "代理智能体的唯一标识符")
    private long agentTypeId;

    // 代理智能体的代码标识，用于内部引用和识别
    @TableField("agent_type_code")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, isNull = false, comment = "代理智能体的代码标识，用于内部引用和识别")
    private String agentTypeCode;

}