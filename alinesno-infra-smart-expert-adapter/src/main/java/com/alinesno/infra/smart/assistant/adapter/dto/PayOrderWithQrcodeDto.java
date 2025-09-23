package com.alinesno.infra.smart.assistant.adapter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 支付订单数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayOrderWithQrcodeDto extends BaseDto {

    /**
     * 充值用户id
     */
    private Long payUserId;

    /**
     * 支付订单号
     */
    private String payOrderId;

    /**
     * 支付方式代码
     */
    private String wayCode;

    /**
     * 支付金额,单位分
     */
    private Long amount;

    /**
     * 支付状态
     */
    private String state;

    /**
     * 向下游回调状态, 0-未发送,  1-已发送
     */
    private Long notifyState;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 商品标题
     */
    private String subject;

    /**
     * 商品描述信息
     */
    private String body;

    /**
     * 渠道支付错误码
     */
    private String errCode;

    /**
     * 渠道支付错误描述
     */
    private String errMsg;

    /**
     * 订单失效时间
     */
    private Date expiredTime;

    /**
     * 订单支付成功时间
     */
    private Date successTime;

    /**
     * 二维码图片URL
     */
    private String qrCodeUrl;

}
