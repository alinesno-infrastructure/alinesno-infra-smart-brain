package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.dto.*;
import com.dtflys.forest.annotation.*;

import java.util.List;

/**
 * AipPoint积分系统消费端，通过消费端获取到积分的管理和统一配置
 */
@BaseRequest(
        baseURL = "#{alinesno.infra.gateway.host}/platform-point" ,
        connectTimeout = 30000,
        readTimeout = 60000)
public interface AipPointConsumer {

    /**
     * 生成订单
     */
    @Post(url = "/api/infra/base/platform/pay/payOrder/createOrder" , contentType = "application/json")
    R<String> createOrder(@JSONBody CreateOrderDto dto);

    /**
     * 查询订单及支付二维码
     */
    @Post(url = "/api/infra/base/platform/pay/payOrder/queryOrderWithQrcode" , contentType = "application/json")
    R<PayOrderWithQrcodeDto> queryOrderWithQrcode(@Query("orderId") String orderId , @Query("wayPay") String wayPay);

    /**
     * 查询订单
     */
    @Post(url = "/api/infra/base/platform/pay/payOrder/queryOrder" , contentType = "application/json")
    R<PayOrderWithQrcodeDto> queryOrder(@Query("orderId") String orderId) ;

    /**
     * 获取当前组织积分
     */
    @Get(url = "/api/base/platform/integral/org/getIntegral" , contentType = "application/json")
    R<OrgPointDto> getIntegral(@Query("orgId") Long orgId);

    /**
     * 消耗积分
     */
    @Post(url = "/api/base/platform/integral/org/consumePoints" , contentType = "application/json")
    R<String> consumePoints(@JSONBody ConsumePointsDto dto);

    /**
     * 查询所有积分套餐
     */
    @Get(url = "/api/base/platform/integral/org/activePackages" , contentType = "application/json")
    R<List<PointPackageDto>> activePackages();

    /**
     * 分页获取组织积分明细
     */
    @Post(url = "/api/base/platform/integral/org/pointsDetail" , contentType = "application/json")
    R<TableDataInfo> pagerIntegralDetail(@JSONBody PointsDetailQueryDto dto);

}
