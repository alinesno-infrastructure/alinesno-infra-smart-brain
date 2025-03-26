package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

/**
 * 上传数据
 */
@Data
public class UploadData {

    private boolean enable; // 是否启用
    private int fileUploadLimit;  // 文件上传限制

    // -->>>>>>>>>>>>> 图片识别处理 -->>>>>>>>>>>>
    private String recognitionType; // 识别方式(ocr/大模型)
    private String modelId;  // 多模态模型ID
    private int imageUploadLimit;  // 图片上传限制

}
