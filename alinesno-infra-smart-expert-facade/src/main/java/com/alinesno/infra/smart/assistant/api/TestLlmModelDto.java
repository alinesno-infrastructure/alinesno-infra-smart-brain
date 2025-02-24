package com.alinesno.infra.smart.assistant.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TestLlmModelDto {

    @NotBlank(message = "模型类型不能为空")
    private String modelType ;  // 模型类型

    @NotBlank(message = "模型提供者不能为空")
    private String providerCode ; // 模型提供者

    @NotBlank(message = "模型接口链接不能为空")
    private String apiUrl ; // 模型接口链接

    @NotBlank(message = "模型接口key不能为空")
    private String apiKey ;  // 模型接口key

    @NotBlank(message = "模型名称不能为空")
    private String model ;  // 模型名称
}
