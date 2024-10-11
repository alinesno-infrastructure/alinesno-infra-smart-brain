package com.alinesno.infra.smart.assistant.adapter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 请求数据集传输对象
 */
@Data
public class RequestDatasetDto {

    @NotBlank(message = "数据集名称不能为空")
    private String datasetName;

    @NotBlank(message = "数据集描述不能为空")
    private String datasetDesc;

    @Min(value = 1 , message = "ownerId不能为空")
    private long ownerId;

    @NotBlank(message = "数据集名称不能为空")
    private String collectionName;

}
