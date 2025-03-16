package com.alinesno.infra.smart.assistant.publish.controller;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.publish.dto.ChannelPublishDTO;
import com.alinesno.infra.smart.assistant.publish.dto.WechatMessageDto;
import com.alinesno.infra.smart.assistant.publish.dto.WechatReplyMessageDto;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;
import com.alinesno.infra.smart.assistant.publish.service.IChannelPublishService;
import com.alinesno.infra.smart.assistant.publish.utils.WechatSignatureUtils;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
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
    public AjaxResult verify(
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
            return AjaxResult.success(echostr);
        } else {
            log.error("签名验证失败，可能不是来自微信服务器的请求");
            return AjaxResult.error("签名验证失败");
        }
    }

    // 处理微信消息
    @PostMapping("/api/infra/smart/assistant/publishWeChat/{apiKey}")
    public AjaxResult handleMessage(
            @PathVariable("apiKey") String apiKey ,
            HttpServletRequest request) {

        log.debug("收到微信消息:{}" , apiKey);

        // 流量限制检查
        if (requestCount.incrementAndGet() > MAX_REQUESTS) {
            log.warn("流量超出限制，拒绝请求");
            requestCount.decrementAndGet();
            return AjaxResult.error("流量超出限制");
        }
        try {
            // 获取请求中的 XML 数据
            StringBuilder xmlData = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    xmlData.append(line);
                }
            }

            // 使用 XStream 将 XML 转换为 WechatMessage 对象
            XStream xStream = new XStream();
            xStream.processAnnotations(WechatMessageDto.class);
            WechatMessageDto message = (WechatMessageDto) xStream.fromXML(xmlData.toString());

            // 自定义回复内容
            String replyContent = "你发送的消息是：" + message.getContent();

            // 构建回复消息对象
            WechatReplyMessageDto replyMessage = new WechatReplyMessageDto();
            replyMessage.setToUserName(message.getFromUserName());
            replyMessage.setFromUserName(message.getToUserName());
            replyMessage.setCreateTime(System.currentTimeMillis() / 1000);
            replyMessage.setContent(replyContent);

            // 使用 XStream 将回复消息对象转换为 XML
            xStream.processAnnotations(WechatReplyMessageDto.class);
            String replyXml = xStream.toXML(replyMessage);

            return AjaxResult.success("消息处理成功", replyXml);
        } catch (IOException e) {
            log.error("处理微信消息时发生 I/O 异常", e);
            return AjaxResult.error("处理微信消息时发生 I/O 异常");
        } catch (Exception e) {
            log.error("处理微信消息时发生未知异常", e);
            return AjaxResult.error("处理微信消息时发生未知异常");
        } finally {
            // 请求处理完成后，减少请求计数
            requestCount.decrementAndGet();
        }
    }
}