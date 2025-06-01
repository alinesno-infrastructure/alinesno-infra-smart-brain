package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Git提交者信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommitterInfo {
    private String name;      // 提交者用户名
    private String email;     // 提交者邮箱
}