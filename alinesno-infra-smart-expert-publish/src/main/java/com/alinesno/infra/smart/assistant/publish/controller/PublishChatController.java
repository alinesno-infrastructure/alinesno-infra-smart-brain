package com.alinesno.infra.smart.assistant.publish.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 渠道聊天控制器，用于控制渠道聊天的请求和响应，包括流量限制等处理。
 */
@Slf4j
@RestController
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/publishChat")
public class PublishChatController {

}