package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import lombok.Data;

// 导入DTO
@Data
public class ExamineeImportDTO {
    private String examineeId;
    private String name;
    private Long groupId;
    private String phone;
}