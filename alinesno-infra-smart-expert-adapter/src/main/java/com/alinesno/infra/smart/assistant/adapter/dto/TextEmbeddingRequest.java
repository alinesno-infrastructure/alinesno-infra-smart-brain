package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

import java.util.List;

/**
 * 文本嵌入请求类
 * 用于表示文本嵌入的请求参数
 */
@Data
public class TextEmbeddingRequest {

    /**
     * 模型名称
     * 用于指定执行文本嵌入的模型
     */
    private String model;

    /**
     * 输入文本
     * 用于指定需要进行嵌入处理的文本内容
     */
    private List<String> input;

    /**
     * 嵌入向量维度
     * 用于指定期望的嵌入向量的维度
     */
    private String dimension;

    /**
     * 编码格式
     * 用于指定文本的编码格式，以确保正确处理文本
     */
    private String encoding_format;

}
