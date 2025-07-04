package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

import java.util.List;

/**
 * 文本重排序请求类
 * 用于表示文本重排序任务的请求数据
 */
@Data
public class TextRerankRequest {

    /**
     * 模型名称，用于指定执行重排序任务所使用的模型
     */
    private String model;

    /**
     * 查询文本，是用户输入的搜索查询或问题
     */
    private String query;

    /**
     * 文档列表，包含待重排序的文档集合
     */
    private List<String> documents;

    /**
     * 是否返回文档内容，指示是否在结果中包含重排序后文档的完整内容
     */
    private boolean return_documents;

    /**
     * 返回的文档数量，指定重排序后返回的文档数目
     */
    private int top_n;

}
