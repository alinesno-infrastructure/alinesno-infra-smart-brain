package com.alinesno.infra.base.search.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
@Data
public class DataProcessingDto {

    /**
     * 数据集ID
     */
    @NotNull(message = "数据集ID不能为空")
    private Long datasetId;

    /**
     * 数据处理方式
     */
    @NotBlank(message = "数据处理方式不能为空")
    private String processingMethod;

    /**
     * 处理参数
     */
    @NotBlank(message = "处理参数不能为空")
    private String processingParam;

    /**
     * 理想分块长度
     */
    @NotNull(message = "理想分块长度不能为空")
    @Positive(message = "理想分块长度必须为正数")
    private String idealChunkLength;

    /**
     * 自定义分割符号
     */
    @NotBlank(message = "自定义分割符号不能为空")
    private String customSplitSymbol;
}