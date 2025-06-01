package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 仓库信息数据传输对象，用于封装Git仓库的基本信息
 *
 * @author luoxiaodong
 * @since 2023-05-31
 */
@Data
public class RepoInfoDto implements Serializable {

    private String gitUrl;          // Git仓库的URL地址
    private String orgName;         // 仓库所属组织或用户名称
    private String repoName;        // 仓库名称
    private String repoFullName;    // 仓库完整名称（组织名/仓库名）
    private String description;     // 仓库描述信息
    private String homepage;        // 仓库主页URL
    private Date createdAt;         // 仓库创建时间
    private Date updatedAt;         // 仓库最后更新时间
    private String defaultBranch;   // 默认分支名称
    private long starCount;         // 仓库星标数量
    private long forkCount;         // 仓库复刻数量
    private long watchCount;        // 仓库关注数量
    private long issueCount;        // 仓库Issue数量
    private String license;         // 仓库许可证信息
}