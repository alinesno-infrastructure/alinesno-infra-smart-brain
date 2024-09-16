package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentVectorDto implements Serializable {

    private long id ; // 主键
    private long dataset_id ; // 所属数据集id
    private String indexName; // 索引名称
    private String document_title ; // 文本标题
    private String document_desc ; // 文本描述
    private String document_content ; // 文本内容
    private String document_embedding ; // 文本向量
    private float score ; // 搜索得分
    private int page ;  // 页码
    private String doc_chunk ; // 文本片段
    private int token_size ; // 文本分词数

    private String sourceFile ; // 文本来源
    private String sourceUrl ; // 文本链接
    private String sourceType ; // 文本类型(pdf/word/html/txt/...)
    private String author ; // 作者

}
