package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Git提交信息DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GitCommitInfoDto extends BaseDto {

    private String projectId ;
    private String projectName ;
    private String commitContent;
    private String committedFiles;
    private String branch;
    private Date commitTime;
    private String committer;
    private String committerEmail;
    private String committerName;
    private String gitCommitMessage;
    private String commitDetails;
    private String commitDiff; // 新增字段，存储代码差异内容
    private String changeType;     // 变更类型(ADD, MODIFY, DELETE等)
    private int diffChunkIndex;    // 当前差异分片索引
    private int diffTotalChunks;   // 差异总分片数

}