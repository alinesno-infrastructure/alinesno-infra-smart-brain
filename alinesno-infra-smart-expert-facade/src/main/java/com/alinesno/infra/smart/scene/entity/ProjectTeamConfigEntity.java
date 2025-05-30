package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_team_config")
@Table(name = "project_team_config")
public class ProjectTeamConfigEntity extends InfraBaseEntity {

    @TableField("user_email")
    @Column(name = "user_email", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "用于与git commit账户关联")
    private String userEmail;

    @TableField("real_name")
    @Column(name = "real_name", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "用户真实姓名")
    private String realName;

    @TableField("job_responsibility")
    @Column(name = "job_responsibility", type = MySqlTypeConstant.TEXT, comment = "描述用户在团队中的工作职责")
    private String jobResponsibility;

    @TableField("project_responsibility")
    @Column(name = "project_responsibility", type = MySqlTypeConstant.TEXT, comment = "描述用户在项目团队中的工作职责")
    private String projectResponsibility;
}    