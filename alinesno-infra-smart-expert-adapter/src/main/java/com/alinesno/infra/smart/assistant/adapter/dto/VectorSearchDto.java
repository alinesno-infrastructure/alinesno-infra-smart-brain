package com.alinesno.infra.smart.assistant.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "数据集ID不能为空")
    private Long datasetId ; // 数据集ID

    private String collectionName ;

    @NotBlank(message = "搜索文本不能为空")
    private String searchText ;

    private String searchType ;

    private Integer topK ;

    private String minRelevance = "0.3";  // 最低相关度

    private Integer quoteLimit = 2000; // 引用查询字符上限

    public VectorSearchDto(long datasetId, String searchText, int topK) {
        this.datasetId = datasetId;
        this.searchText = searchText;
        this.topK = topK;
    }
}
