package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 考生分组信息
 * 对应数据库中的 examinee_group 表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("examinee_group")
public class ExamineeGroupEntity extends InfraBaseEntity {

    // 分组名称，所属场景
    @TableField("group_name")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 64, comment = "分组名称")
    private String groupName;


    @TableField("scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "所属场景")
    private String sceneId;

}