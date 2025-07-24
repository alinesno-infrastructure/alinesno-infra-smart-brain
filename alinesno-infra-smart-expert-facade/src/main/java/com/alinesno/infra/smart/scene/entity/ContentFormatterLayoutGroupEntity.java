package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容格式化分组配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("content_formatter_layout_group")
public class ContentFormatterLayoutGroupEntity extends InfraBaseEntity {

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

    @TableField
    @Column(comment = "分组类型(layout排版分组|audit审核分组)" , length = 50 , type = MySqlTypeConstant.VARCHAR)
    private String groupType ; // 分组类型

    // 数据范围（是否为公开或者为组织所用)
    @TableField
    @Column(name = "data_scope", type = MySqlTypeConstant.VARCHAR, length = 16, comment = "数据范围")
    private String dataScope ;

    public static ContentFormatterLayoutGroupEntity toPower(InfraBaseEntity entity) {
        ContentFormatterLayoutGroupEntity template = new ContentFormatterLayoutGroupEntity();

        template.setOrgId(entity.getOrgId());
        template.setOperatorId(entity.getOperatorId());
        template.setDepartmentId(entity.getDepartmentId());

        return template;
    }


}