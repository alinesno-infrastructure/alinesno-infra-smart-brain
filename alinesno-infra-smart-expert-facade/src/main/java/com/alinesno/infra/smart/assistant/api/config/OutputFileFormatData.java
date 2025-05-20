package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;

import java.util.List;

/**
 * OutputFileFormatData类用于存储输出文件格式的相关配置信息
 */
@Data
public class OutputFileFormatData {
    /**
     * 是否启用输出文件格式转换功能
     */
    private boolean enable;

    /**
     * 用于文件格式转换的大型语言模型(LLM)名称
     */
    private String llmModel;

    /**
     * 支持的文件格式列表
     */
    private List<String> fileFormats;
}
