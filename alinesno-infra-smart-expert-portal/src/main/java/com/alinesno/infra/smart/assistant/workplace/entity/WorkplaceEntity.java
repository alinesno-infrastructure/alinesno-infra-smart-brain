package com.alinesno.infra.smart.assistant.workplace.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作台实体类，针对于具体的工作台业务场景配置管理
 */
@EqualsAndHashCode(callSuper = true)
@TableName("workplace")
@Data
public class WorkplaceEntity extends InfraBaseEntity {

    /**
     * 工作台名称
     */
    @TableField
    @Column(name = "name", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "工作台名称")
    private String name;

    /**
     * 工作台描述
     */
    @TableField
    @Column(name = "description", type = MySqlTypeConstant.VARCHAR, length = 128 , comment = "工作台描述")
    private String description;

    /**
     * 工作台背景图
     */
    @TableField
    @Column(name = "background_image", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "工作台背景图")
    private String backgroundImage;

    /**
     * 工作台类型
     */
    @TableField
    @Column(name = "workplace_type", type = MySqlTypeConstant.VARCHAR, length = 10, comment = "工作台类型")
    private String workplaceType ;

    /**
     * 工作台来源（1首页获取/2后台添加）
     */
    @TableField
    @Column(name = "workplace_source", type = MySqlTypeConstant.VARCHAR, length = 10, comment = "工作台来源")
    private String workplaceSource ;

    /**
     * 来源工作台ID
     */
    @TableField
    @Column(name = "source_workplace_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "来源工作台ID")
    private long sourceWorkplaceId ; // 来源工作台ID
}