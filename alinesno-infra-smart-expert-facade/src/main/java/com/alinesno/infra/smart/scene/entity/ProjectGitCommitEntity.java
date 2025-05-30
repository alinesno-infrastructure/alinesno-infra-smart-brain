package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project_git_commit")
@Table(name = "project_git_commit")
public class ProjectGitCommitEntity extends InfraBaseEntity {

    @TableField("project_id")
    @Column(name = "project_id", type = MySqlTypeConstant.BIGINT, comment = "项目id")
    private Long projectId;

    @TableField("commit_content")
    @Column(name = "commit_content", type = MySqlTypeConstant.TEXT, comment = "提交内容")
    private String commitContent;

    @TableField("committed_files")
    @Column(name = "committed_files", type = MySqlTypeConstant.TEXT, comment = "提交的文件")
    private String committedFiles;

    @TableField("content_analysis")
    @Column(name = "content_analysis", type = MySqlTypeConstant.TEXT, comment = "提交的内容解析(AI解析内容)")
    private String contentAnalysis;

    @TableField("commit_time")
    @Column(name = "commit_time", type = MySqlTypeConstant.DATETIME, comment = "提交时间")
    private Date commitTime;

    @TableField("committer")
    @Column(name = "committer", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "提交人")
    private String committer;

    @TableField("committer_email")
    @Column(name = "committer_email", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "提交人邮箱")
    private String committerEmail;

    @TableField("committer_name")
    @Column(name = "committer_name", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "提交人姓名")
    private String committerName;

    @TableField("commit_details")
    @Column(name = "commit_details", type = MySqlTypeConstant.TEXT, comment = "提交信息详情")
    private String commitDetails;
}    