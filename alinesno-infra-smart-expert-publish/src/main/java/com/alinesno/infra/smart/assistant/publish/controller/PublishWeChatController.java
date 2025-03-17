package com.alinesno.infra.smart.assistant.publish.controller;

import cn.hutool.http.server.HttpServerResponse;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.publish.dto.ChannelPublishDTO;
import com.alinesno.infra.smart.assistant.publish.dto.WechatMessageDto;
import com.alinesno.infra.smart.assistant.publish.dto.WechatReplyMessageDto;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;
import com.alinesno.infra.smart.assistant.publish.service.IChannelPublishService;
import com.alinesno.infra.smart.assistant.publish.utils.EventDispatcher;
import com.alinesno.infra.smart.assistant.publish.utils.MessageUtil;
import com.alinesno.infra.smart.assistant.publish.utils.MsgDispatcher;
import com.alinesno.infra.smart.assistant.publish.utils.WechatSignatureUtils;
import com.thoughtworks.xstream.XStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PublishWeChatController {

    @Autowired
    private IChannelPublishService channelPublishService ;

    // 简单模拟流量限制，单位时间内允许的最大请求数
    private static final int MAX_REQUESTS = 100;
    // 计数器，用于记录当前时间窗口内的请求数量
    private final AtomicInteger requestCount = new AtomicInteger(0);

    // 处理微信服务器验证
    @GetMapping("/api/infra/smart/assistant/publishWeChat/{apiKey}")
    public String verify(
                @PathVariable("apiKey") String apiKey ,
                @RequestParam("signature") String signature,
                @RequestParam("timestamp") String timestamp,
                @RequestParam("nonce") String nonce,
                @RequestParam("echostr") String echostr) {

        log.debug("收到微信服务器的验证请求:{}" , apiKey);

        ChannelPublishEntity channel = channelPublishService.getByShareToken(apiKey) ;
        ChannelPublishDTO channelDto = ChannelPublishDTO.toDto(channel) ;

        // 进行签名验证
        if (WechatSignatureUtils.validateSignature(signature, timestamp, nonce, channelDto.getToken())) {
            return echostr ;
        } else {
            log.error("签名验证失败，可能不是来自微信服务器的请求");
            return null ;
        }
    }

    // 处理微信消息
    @PostMapping("/api/infra/smart/assistant/publishWeChat/{apiKey}")
    public void handleMessage(
            @PathVariable("apiKey") String apiKey,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.debug("收到微信消息: {}", apiKey);

        ChannelPublishEntity channel = channelPublishService.getByShareToken(apiKey) ;
        ChannelPublishDTO channelDto = ChannelPublishDTO.toDto(channel) ;

        // 流量限制检查
        if (requestCount.incrementAndGet() > MAX_REQUESTS) {
            log.warn("流量超出限制，拒绝请求");
            requestCount.decrementAndGet();
            try {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "流量超出限制");
            } catch (Exception e) {
                log.error("发送流量超出限制响应时出错", e);
            }
            return;
        }

        try {
            // 解析微信消息
            Map<String, String> messageMap = MessageUtil.parseXml(request);
            log.debug("解析到的消息内容: {}", messageMap.get("Content"));

            String type = messageMap.get("MsgType");
            if (MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(type)) {
                // 事件处理
                EventDispatcher.processEvent(messageMap);
            } else {
                // 设置请求和响应的字符编码
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("UTF-8");
                // 消息处理
                String data = MsgDispatcher.processMessage(messageMap , channelDto.getAppId() , channelDto.getSecret() , apiKey);
                try (PrintWriter out = response.getWriter()) {
                    out.print(data);
                }
            }
        } catch (Exception e) {
            log.error("处理微信消息时发生异常", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "处理微信消息时发生异常");
            } catch (Exception ex) {
                log.error("发送异常响应时出错", ex);
            }
        } finally {
            // 请求处理完成后，减少请求计数
            requestCount.decrementAndGet();
        }
    }

}