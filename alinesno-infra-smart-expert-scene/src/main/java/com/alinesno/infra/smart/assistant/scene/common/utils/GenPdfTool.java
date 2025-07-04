package com.alinesno.infra.smart.assistant.scene.common.utils;

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

@Slf4j
public class GenPdfTool {

    private static final RestTemplate restTemplate = new RestTemplate();

    @NotNull
    public static ResponseEntity<Resource> getResourceResponseEntity(String storageId , String previewUrl, Long taskId) {

        byte[] fileBytes = restTemplate.getForObject(previewUrl, byte[].class);

        if (fileBytes == null) {
            // 文件下载失败，返回合适的错误信息
            return ResponseEntity.notFound().build();
        }

        // 检查文件字节数组长度是否合理，这里只是简单示例，你可能需要更准确的判断
        if (fileBytes.length < 1024) { // 假设小于 1KB 的文件不太可能是有效的.docx
            return ResponseEntity.badRequest().build();
        }

        File docFile = null;
        File pdfFile = null;
        try {
            // 创建临时 docx 文件
            docFile = File.createTempFile("temp-docx", ".docx");
            Files.write(docFile.toPath(), fileBytes);

            // 创建临时 pdf 文件
            pdfFile = File.createTempFile("temp-pdf", ".pdf");
            WordToPdfConverter.convertToPdf(docFile, pdfFile);

            // 读取 PDF 文件内容
            byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

            HttpHeaders headers = new HttpHeaders();
            // 设置正确的 MIME 类型
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=preview.pdf");

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            log.error("文件处理出错", e);
            return ResponseEntity.status(500).build();
        } finally {
            // 删除临时文件
            if (docFile != null) {
                docFile.delete();
            }
            if (pdfFile != null) {
                pdfFile.delete();
            }
        }
    }

    @NotNull
    public static StringBuilder getGenDocReviewMarkdownReport(String documentName,
                                                              String auditOption,
                                                              String dateTime,
                                                              List<DocReviewRulesEntity> rules,
                                                              List<DocReviewAuditResultEntity> auditResultList,
                                                              long taskId) {
        StringBuilder markdown = new StringBuilder();

        // 封面
        markdown.append("# 审核报告\n");
        markdown.append("\n");
        if (rules != null) {
            markdown.append("- 审核规则总数：").append(rules.size()).append("\n");
            Map<String, Map<String, Integer>> riskCountMap = new HashMap<>();
            Map<Long, Integer> ruleResultCountMap = new HashMap<>(); // 用于统计每条规则下的审核结果数量

            for (DocReviewRulesEntity rule : rules) {
                String riskLevel = rule.getRiskLevel();
                riskCountMap.putIfAbsent(riskLevel, new HashMap<>());
                Map<String, Integer> countMap = riskCountMap.get(riskLevel);
                countMap.put(rule.getRuleName(), countMap.getOrDefault(rule.getRuleName(), 0) + 1);

                int resultCount = 0;
                for (DocReviewAuditResultEntity result : auditResultList) {
                    if (result.getRuleId().equals(rule.getId())) {
                        resultCount++;
                    }
                }
                ruleResultCountMap.put(rule.getId(), resultCount);
            }

            for (Map.Entry<String, Map<String, Integer>> riskEntry : riskCountMap.entrySet()) {
                String riskLevel = riskEntry.getKey();
                int totalCount = riskEntry.getValue().values().stream().mapToInt(Integer::intValue).sum();
                markdown.append("- ").append(riskLevel).append("风险规则数：").append(totalCount).append("\n");
            }

            markdown.append("\n");
            markdown.append("### 每条规则需修改数据统计\n");
            for (DocReviewRulesEntity rule : rules) {
                String shortRuleName = rule.getRuleName().length() > 20 ? rule.getRuleName().substring(0, 20) + "..." : rule.getRuleName();
                int resultCount = ruleResultCountMap.getOrDefault(rule.getId(), 0);
                markdown.append("- ").append(shortRuleName).append("：").append(resultCount).append(" 项需修改\n");
            }
        }

        markdown.append("\n");
        markdown.append("## 基本信息\n");
        markdown.append("\n");
        markdown.append("- **审核文件名称**：").append(documentName).append("\n");
        markdown.append("- **审核方式**：").append(auditOption).append("\n");
        markdown.append("- **审核时间**：").append(dateTime).append("\n");
        markdown.append("\n");

        markdown.append("\n");
        markdown.append("## 审核规则及结果\n");
        markdown.append("\n");

        if(rules != null){
            for (DocReviewRulesEntity rule : rules) {
                // 处理规则名称过长的问题
                String shortRuleName = rule.getRuleName().length() > 20 ? rule.getRuleName().substring(0, 20) + "..." : rule.getRuleName();
                markdown.append("\n");
                markdown.append("### ").append(shortRuleName).append("\n");
                markdown.append("\n");
                markdown.append("- **规则内容**：").append(rule.getRuleContent()).append("\n");
                markdown.append("- **风险级别**：").append(rule.getRiskLevel()).append("\n");
//                markdown.append("- **审核立场**：").append(rule.getReviewPosition()).append("\n");
                markdown.append("\n");
                markdown.append("#### 审核结果\n");
                markdown.append("\n");
                for (DocReviewAuditResultEntity result : auditResultList) {
                    if (result.getRuleId().equals(rule.getId())) {
                        markdown.append("\n");
                        markdown.append("#### 修改原因\n");
                        markdown.append("\n");
                        markdown.append(result.getModificationReason()).append("\n");
                        markdown.append("\n");
                        markdown.append("- **原内容**：").append(result.getOriginalContent()).append("\n");
                        markdown.append("\n");
                        markdown.append("- **建议修改的内容**：").append(result.getSuggestedContent()).append("\n");
                    }
                }
            }
        }

        return markdown ;
    }
}
