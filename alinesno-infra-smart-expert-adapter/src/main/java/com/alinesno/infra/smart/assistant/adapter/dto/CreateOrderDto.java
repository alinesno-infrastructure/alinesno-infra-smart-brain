package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

/**
 * 创建订单数据传输对象
 */
@Data
public class CreateOrderDto {
    private Long payUserId ;
    private Long orgId ;
    private String wayCode ; // 微信或者支付宝
    private Long amount ;
    private String clientIp ;
    private String subject ; // 商品标题
    private Long packageId ; // 套餐ID
}
