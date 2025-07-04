package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@TableName("long_text_group_templates")
@Data
public class LongTextTemplateGroupEntity extends InfraBaseEntity {

    // 分组名称、分组图标、排序

    @TableField
    @Column(comment = "分组名称" , length = 50 , type = MySqlTypeConstant.VARCHAR)
    private String name;

    @TableField
    @Column(comment = "分组图标" , length = 128 , type = MySqlTypeConstant.VARCHAR)
    private String icon;

    @TableField
    @Column(comment = "排序" , length = 11 , type = MySqlTypeConstant.INT)
    private Integer sort;

    public static LongTextTemplateGroupEntity toPower(InfraBaseEntity entity) {
        LongTextTemplateGroupEntity template = new LongTextTemplateGroupEntity();

        template.setOrgId(entity.getOrgId());
        template.setOperatorId(entity.getOperatorId());
        template.setDepartmentId(entity.getDepartmentId());

        return template;
    }

}
