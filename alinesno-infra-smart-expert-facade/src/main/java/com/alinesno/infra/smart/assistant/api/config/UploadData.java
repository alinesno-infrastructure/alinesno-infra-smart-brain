package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

/**
 * 上传数据
 */
@Data
public class UploadData {

    private boolean enable; // 是否启用
    private String modelId;  // 多模态模型ID
    private int fileUploadLimit;  // 文件上传限制
    private int imageUploadLimit;  // 图片上传限制

}
