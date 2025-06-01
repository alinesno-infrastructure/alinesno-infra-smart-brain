package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import lombok.Data;

@Data
public class GitFileDiffDto {
    private String filePath;       // 文件路径
    private String diffContent;    // 差异内容
    private String changeType;     // 变更类型(ADD, MODIFY, DELETE等)
}