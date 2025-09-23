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

    // 识别结果处理
    private int maxLength = 16384; // 最大长度，默认16k

    // 超出字符策略(truncate截取|rag转换成RAG知识库)
    private String overflowStrategy = "truncate";

    // rag向量化ID
    private String ragVectorId ;

    // 获取最大长度
    public int getMaxLength() {
        return maxLength == 0 ? 16384 : maxLength;
    }
}
