//package com.alinesno.infra.base.search.vector.dto;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.Data;
//
//@Data
//public class VectorSearchDto {
//
//    @NotNull(message = "数据集ID不能为空")
//    private Long datasetId ; // 数据集ID
//
//    private String collectionName ;
//
//    @NotBlank(message = "搜索文本不能为空")
//    private String searchText ;
//
//    private Integer topK ;
//
//    private String minRelevance = "0.6";  // 最低相关度
//
//    private Integer quoteLimit; // 引用查询字符上限
//}
