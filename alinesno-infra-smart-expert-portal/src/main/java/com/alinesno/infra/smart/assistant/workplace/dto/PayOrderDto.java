package com.alinesno.infra.smart.assistant.workplace.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 订单信息
 */
@Data
public class PayOrderDto {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String subject; // 商品名称

    @NotBlank(message = "支付方式不能为空")
    @Size(max = 20, message = "支付方式长度不能超过20个字符")
    private String payType; // 支付方式

    @NotNull(message = "支付金额不能为空")
    @Min(value = 1, message = "支付金额必须大于0")
    private Long payAmount; // 支付金额

    @NotNull(message = "套餐ID不能为空")
    @Min(value = 1, message = "套餐ID必须大于0")
    private Long packageId; // 套餐ID
}