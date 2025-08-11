package com.alinesno.infra.smart.assistant.scene.common.utils;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditResultEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class GenPdfTool {

    private static final RestTemplate restTemplate = new RestTemplate();

    @NotNull
    public static StringBuilder getGenDocReviewMarkdownReport(String documentName,
                                                              String auditOption,
                                                              String dateTime,
                                                              List<DocReviewRulesEntity> rules,
                                                              List<DocReviewAuditResultEntity> auditResultList,
                                                              long taskId) {
        StringBuilder markdown = new StringBuilder();

        // 封面
        markdown.append("# 文档审核报告\n");
        markdown.append("\n");
        markdown.append("## 基本信息\n");
        markdown.append("\n");
        markdown.append("- **审核文件名称**: ").append(documentName).append("\n");
        markdown.append("- **审核方式**: ").append(auditOption).append("\n");
        markdown.append("- **审核时间**: ").append(dateTime).append("\n");
        markdown.append("\n");

        if (rules != null) {
            // 审核规则统计
            markdown.append("## 审核规则统计\n");
            markdown.append("\n");
            markdown.append("- **审核规则总数**: ").append(rules.size()).append("\n");

            // 按风险级别统计
            Map<String, Long> riskLevelCount = rules.stream()
                    .collect(Collectors.groupingBy(DocReviewRulesEntity::getRiskLevel, Collectors.counting()));

            riskLevelCount.forEach((riskLevel, count) -> {
                markdown.append("- **").append(riskLevel).append("风险规则数**: ").append(count).append("\n");
            });
            markdown.append("\n");

            // 每条规则需修改数据统计 - 使用表格
            markdown.append("## 规则问题统计\n");
            markdown.append("\n");
            markdown.append("| 序号 | 规则名称 | 风险级别 | 需修改项数 |\n");
            markdown.append("|------|----------|----------|------------|\n");

            Map<Long, Integer> ruleResultCountMap = new HashMap<>();
            for (DocReviewRulesEntity rule : rules) {
                int resultCount = (int) auditResultList.stream()
                        .filter(result -> result.getRuleId().equals(rule.getId()))
                        .count();
                ruleResultCountMap.put(rule.getId(), resultCount);
            }

            int index = 1;
            for (DocReviewRulesEntity rule : rules) {
                int resultCount = ruleResultCountMap.getOrDefault(rule.getId(), 0);
                markdown.append("| ").append(index++).append(" | ")
                        .append(rule.getRuleName()).append(" | ")
                        .append(rule.getRiskLevel()).append(" | ")
                        .append(resultCount).append(" |\n");
            }
            markdown.append("\n");

            // 详细审核结果
            markdown.append("## 详细审核结果\n");
            markdown.append("\n");

            index = 1;
            for (DocReviewRulesEntity rule : rules) {
                List<DocReviewAuditResultEntity> ruleResults = auditResultList.stream()
                        .filter(result -> result.getRuleId().equals(rule.getId()))
                        .toList();

                if (!ruleResults.isEmpty()) {
                    markdown.append("### ").append(index++).append(". ").append(rule.getRuleName()).append("\n");
                    markdown.append("\n");
                    markdown.append("- **规则内容**: ").append(rule.getRuleContent()).append("\n");
                    markdown.append("- **风险级别**: ").append(rule.getRiskLevel()).append("\n");
                    markdown.append("\n");

                    int resultIndex = 1;
                    for (DocReviewAuditResultEntity result : ruleResults) {
                        markdown.append("#### 问题").append(resultIndex++).append("\n");
                        markdown.append("\n");
                        markdown.append("- **问题描述**: ").append(result.getModificationReason()).append("\n");
                        markdown.append("- **风险等级**: ").append(result.getRiskLevel()).append("\n");
                        markdown.append("\n");
                        markdown.append("**原内容**: \n");
                        markdown.append("```\n");
                        markdown.append(result.getOriginalContent()).append("\n");
                        markdown.append("```\n");
                        markdown.append("\n");
                        markdown.append("**建议修改内容**: \n");
                        markdown.append("```\n");
                        markdown.append(result.getSuggestedContent()).append("\n");
                        markdown.append("```\n");
                        markdown.append("\n");
                    }
                }
            }
        }

        return markdown;
    }

}
