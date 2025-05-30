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

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_manager")
@Table(name = "project_manager")
public class ProjectManagerEntity extends InfraBaseEntity {

    @TableField("timing_strategy")
    @Column(name = "timing_strategy", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "定时任务的策略")
    private String timingStrategy;

    @TableField("project_repo_url")
    @Column(name = "project_repo_url", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "项目git地址/svn地址(默认git)")
    private String projectRepoUrl;

    @TableField("project_type")
    @Column(name = "project_type", type = MySqlTypeConstant.VARCHAR, length = 20, comment = "项目类型(svn/git)")
    private String projectType;

    @TableField("auth_username")
    @Column(name = "auth_username", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "项目认证用户名")
    private String authUsername;

    @TableField("auth_password")
    @Column(name = "auth_password", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "项目认证密码")
    private String authPassword;

    @TableField("last_sync_time")
    @Column(name = "last_sync_time", type = MySqlTypeConstant.DATETIME, comment = "最后同步时间")
    private Date lastSyncTime;
}    