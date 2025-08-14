package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述：智能助手模板分组
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@TableName("general_agent_group_templates")
@Data
public class GeneralAgentTemplateGroupEntity extends InfraBaseEntity {

    // 分组名称、分组图标、排序

    @TableField
    @Column(comment = "分组名称" , length = 50 , type = MySqlTypeConstant.VARCHAR)
    private String name;

    @TableField
    @Column(comment = "分组描述" , length = 128 , type = MySqlTypeConstant.VARCHAR)
    private String remark;

    @TableField
    @Column(comment = "分组图标" , length = 128 , type = MySqlTypeConstant.VARCHAR)
    private String icon;

    @TableField
    @Column(comment = "排序" , length = 11 , type = MySqlTypeConstant.INT)
    private Integer sort;

    public static GeneralAgentTemplateGroupEntity toPower(InfraBaseEntity entity) {
        GeneralAgentTemplateGroupEntity template = new GeneralAgentTemplateGroupEntity();

        template.setOrgId(entity.getOrgId());
        template.setOperatorId(entity.getOperatorId());
        template.setDepartmentId(entity.getDepartmentId());

        return template;
    }

}
