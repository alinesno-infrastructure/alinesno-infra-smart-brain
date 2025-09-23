package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.smart.assistant.adapter.AipPointConsumer;
import com.alinesno.infra.smart.assistant.adapter.dto.*;
import com.alinesno.infra.smart.assistant.workplace.dto.PayOrderDto;
import com.alinesno.infra.smart.assistant.workplace.tools.QRCodeGenerator;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 业务积分控制器
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping(value = "/v1/api/infra/base/im/point/")
public class ImAipPointController {

    @Autowired
    private AipPointConsumer aipPointConsumer;

    @Autowired
    private IAccountPointService accountPointService ;

    /**
     * 生成业务订单
     */
    @PostMapping("/createOrder")
    public AjaxResult createOrder(@RequestBody @Validated PayOrderDto dto , HttpServletRequest request) {

        Long userId = CurrentAccountJwt.getUserId() ;
        Long orgId = CurrentAccountJwt.get().getOrgId() ;

        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setPayUserId(userId);
        createOrderDto.setOrgId(orgId);
        createOrderDto.setWayCode(dto.getPayType());
        createOrderDto.setAmount(dto.getPayAmount());
        createOrderDto.setClientIp(request.getRemoteHost());
        createOrderDto.setSubject(dto.getSubject());
        createOrderDto.setPackageId(dto.getPackageId());

        String orderId = aipPointConsumer.createOrder(createOrderDto).getData();
        return AjaxResult.success("生成业务订单成功" , orderId);
    }

    /**
     * 查询订单信息并获取到base64的支付二维码
     */
    @SneakyThrows
    @GetMapping("/queryOrderWithQrcode")
    public AjaxResult queryOrderWithQrcode(@RequestParam String orderId ,
                                           @RequestParam String payWay) {

        R<PayOrderWithQrcodeDto> dtoData = aipPointConsumer.queryOrderWithQrcode(orderId , payWay) ;
        String base64 = QRCodeGenerator.getQRCodeImageAsBase64(dtoData.getData().getQrCodeUrl(), 250, 250);

        AjaxResult result = AjaxResult.success("查询订单信息并获取到base64的支付二维码成功" , dtoData.getData());
        result.put("qrcodeBase64" , base64) ;

        return result ;
    }

    /**
     * 查询订单
     */
    @GetMapping("/queryOrder")
    public AjaxResult queryOrder(@RequestParam String orderId) {
        R<PayOrderWithQrcodeDto> dtoData = aipPointConsumer.queryOrder(orderId) ;
        return AjaxResult.success("查询订单成功" , dtoData.getData());
    }

    /**
     * 获取到组织积分
     */
    @GetMapping("/getIntegral")
    public AjaxResult getIntegral() {

        if(!accountPointService.isOpenPoint()){
            AjaxResult result = AjaxResult.success("组织积分功能未开启") ;
            result.put("openPoint" , false) ;
            return result ;
        }

        Long orgId = CurrentAccountJwt.get().getOrgId() ;

        R<OrgPointDto> dtoData = aipPointConsumer.getIntegral(orgId) ;
        AjaxResult result =  AjaxResult.success("获取到组织积分成功" , dtoData.getData());
        result.put("openPoint" , accountPointService.isOpenPoint());

        return result ;
    }

    /**
     * 获取到积分套餐
     */
    @GetMapping("/activePackages")
    public AjaxResult activePackages() {
        R<List<PointPackageDto>> dtoData = aipPointConsumer.activePackages() ;
        return AjaxResult.success("获取到积分套餐成功" , dtoData.getData());
    }

    /**
     * 分页获取组织积分明细
     */
    @PostMapping("/pointsDetail")
    public TableDataInfo pointsDetail(@RequestBody PointsDetailQueryDto dto) {

        Long orgId = CurrentAccountJwt.get().getOrgId() ;
        dto.setOrgId(orgId);

        R<TableDataInfo> dtoData = aipPointConsumer.pagerIntegralDetail(dto) ;

        return dtoData.getData();

    }
}
