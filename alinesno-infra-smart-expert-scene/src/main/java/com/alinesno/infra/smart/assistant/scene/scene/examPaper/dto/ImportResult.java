package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 导入结果类
@Data
public class ImportResult {
    private int successCount;
    private List<String> inputDuplicates = new ArrayList<>();
    private List<String> dbDuplicates = new ArrayList<>();

    public boolean hasDuplicates() {
        return !inputDuplicates.isEmpty() || !dbDuplicates.isEmpty();
    }
}