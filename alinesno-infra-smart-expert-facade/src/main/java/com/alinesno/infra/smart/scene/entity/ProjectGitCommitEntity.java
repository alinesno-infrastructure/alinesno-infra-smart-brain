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
@Table(comment = "项目Git提交记录")
public class ProjectGitCommitEntity extends InfraBaseEntity {

    @TableField("project_id")
    @Column(name = "project_id", type = MySqlTypeConstant.BIGINT, comment = "项目id")
    private Long projectId;

    // 项目名称
    @TableField("project_name")
    @Column(name = "project_name", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "项目名称")
    private String projectName;

    @TableField("committed_files")
    @Column(name = "committed_files", type = MySqlTypeConstant.TEXT, comment = "提交的文件")
    private String committedFiles;

    @TableField("commit_content")
    @Column(name = "commit_content", type = MySqlTypeConstant.TEXT, comment = "提交内容")
    private String commitContent;

    @TableField("commit_details")
    @Column(name = "commit_details", type = MySqlTypeConstant.TEXT, comment = "提交详情")
    private String commitDetails;

    @TableField("git_commit_message")
    @Column(type = MySqlTypeConstant.TEXT, comment = "GitCommit信息")
    private String gitCommitMessage ;

    @TableField("branch")
    @Column(name = "branch", type = MySqlTypeConstant.VARCHAR, length = 100, comment = "分支")
    private String branch;

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

    @TableField("commit_diff")
    @Column(name = "commit_diff", type = MySqlTypeConstant.TEXT, comment = "提交差异")
    private String commitDiff ;

    @TableField("change_type")
    @Column(name = "change_type", type = MySqlTypeConstant.VARCHAR, length = 20, comment = "变更类型")
    private String changeType;     // 变更类型(ADD, MODIFY, DELETE等)

    @TableField("diff_chunk_index")
    @Column(name = "diff_chunk_index", type = MySqlTypeConstant.INT, comment = "差异分片索引")
    private int diffChunkIndex;    // 当前差异分片索引

    @TableField("diff_total_chunks")
    @Column(name = "diff_total_chunks", type = MySqlTypeConstant.INT, comment = "差异总分片数")
    private int diffTotalChunks;   // 差异总分片数

}