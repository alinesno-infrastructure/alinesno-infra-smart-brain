package com.alinesno.infra.smart.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
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
public class AgentSceneEntity extends InfraBaseEntity {

    // 代理智能体的唯一标识符
    @TableField("agent_id")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "Agent ID")
    private Long agentId;

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

    @TableField("sort_order")
    @Column(type = MySqlTypeConstant.INT, isNull = false, comment = "排序")
    private Integer sortOrder;

    // 添加角色所属的组织（只为当前组织可以看到)
    @TableField("role_organization_id")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "角色所属组织")
    private Long roleOrganizationId;

    // 场景下的LLM模型ID
    @TableField("llm_model_id")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "LLM模型ID")
    private long llmModelId ;

    // 场景下的图片模型ID
    @TableField("image_model_id")
    @Column(type = MySqlTypeConstant.BIGINT, isNull = false, comment = "图片模型ID")
    private long imageModelId ;

    // 场景范围
    @TableField("scene_scope")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, isNull = false, comment = "场景范围")
    private String sceneScope ;

}