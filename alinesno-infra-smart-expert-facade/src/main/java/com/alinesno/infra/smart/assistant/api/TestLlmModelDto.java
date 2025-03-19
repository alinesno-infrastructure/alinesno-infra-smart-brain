package com.alinesno.infra.smart.assistant.api;

import jakarta.validation.constraints.Min;
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

    @Min(value = 1 , message = "测试频道ID不能为空")
    private long testChannelId;

    // 图像处理服务的密钥，用于验证身份
    private String secretKey ;

    // 语音合成，发音人
    private String voice ;

}
