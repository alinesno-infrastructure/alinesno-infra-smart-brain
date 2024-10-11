package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用于向量搜索的DTO类
 * 这个类封装了向量搜索请求的必要参数，以便在智能助手服务中使用
 */
@ToString
@NoArgsConstructor
@Data
public class VectorSearchDto {

    /**
     * 数据集的唯一标识符
     * 用于指定要进行向量搜索的数据集
     */
    private long datesetId;

    /**
     * 用户输入的搜索文本
     * 用于在数据集中查找与该文本相关的项
     */
    private String searchText;

    /**
     * 搜索结果中返回的顶级匹配项的数量
     * 用于指定返回结果的数量，表示返回多少个最相关的项
     */
    private int topK;

    public VectorSearchDto(long datesetId, String searchText, int topK) {
        this.datesetId = datesetId;
        this.searchText = searchText;
        this.topK = topK;
    }
}
