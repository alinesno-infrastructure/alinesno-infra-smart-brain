package com.alinesno.infra.base.search.api;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class SearchUpdateConfigDto {

    // 验证数据集 ID，必须是正整数
    @NotNull(message = "数据集 ID 不能为空")
    private Long datasetId;

    // 验证搜索类型，必须是 SearchType 枚举中的值
    @NotNull(message = "搜索类型不能为空")
    private String searchType;

    // 验证引用上限，必须是正整数，且在 1 到 10000 之间
    @Min(value = 1, message = "引用上限不能小于 1")
    @Max(value = 10000, message = "引用上限不能大于 10000")
    @NotNull(message = "引用上限不能为空")
    private Integer quoteLimit;

    // 验证是否结果重排，必须是布尔类型
    @NotNull(message = "是否结果重排不能为空")
    private Boolean reorderResults;

    @NotNull(message = "最低相关度不能为空")
    private String minRelevance;

}