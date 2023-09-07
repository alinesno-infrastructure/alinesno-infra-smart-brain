package com.alinesno.infra.smart.brain.api.controller;

import com.alinesno.infra.smart.brain.api.VerityTokenDto;
import com.alinesno.infra.smart.brain.api.interceptor.GPT;
import com.alinesno.infra.smart.brain.api.reponse.ChatResponse;
import com.alinesno.infra.smart.brain.api.session.UserSession;
import com.alinesno.infra.smart.brain.entity.AccountEntity;
import com.alinesno.infra.smart.brain.service.IAccountService;
import com.alinesno.infra.smart.brain.service.IChatgptApiService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 获取chatgpt接口。
 *
 *@author luoxiaodong
 *@version 1.0.0
 */
@RestController
public class ChatgptApiController {

	private static final Logger log = LoggerFactory.getLogger(ChatgptApiController.class);

	@Autowired
	private IChatgptApiService chatgptApiService;

	@Autowired
	private IAccountService accountService;

	private static final String defaultNewUserCount = "alinesno.ai.gpt.defaultCount";

	/**
	 * 获取用户信息。
	 *
	 * @return ChatResponse 对象
	 */
	@GPT
	@RequestMapping("/api/userInfo")
	public ChatResponse userInfo(HttpServletRequest request) {
		String tokenId = UserSession.getToken(request);
		log.debug("tokenId = {}", tokenId);

		AccountEntity user = new AccountEntity();
		user.setUsername("咨询员");
		user.setAvatar("http://data.linesno.com/avatar.jpg");
		user.setDescription("我是一名什么都咨询的大神大人.");

		return ChatResponse.success(user);
	}

	/**
	 * 获取配置信息。
	 *
	 * @return ChatResponse 对象
	 */
	@GPT
	@RequestMapping("/api/config")
	public ChatResponse config(HttpServletRequest request) {
		String tokenId = UserSession.getToken(request);
		log.debug("tokenId = {}", tokenId);

		return accountService.usage(tokenId);
	}

	/**
	 * 获取验证码。
	 *
	 * @param token 验证令牌数据传输对象
	 * @return ChatResponse 对象
	 */
	@RequestMapping("/api/getVerifyCode")
	public ChatResponse getVerifyCode(@RequestBody VerityTokenDto token) {
		String defaultConfig = "100" ; // authorityConfigClient.getConfigValueByKey(defaultNewUserCount);

		boolean b = accountService.initAccount(token.getEmail(), defaultConfig);

		return b ? ChatResponse.success() : ChatResponse.successMsg("邮件发送验证失败.");
	}

	/**
	 * 验证令牌。
	 *
	 * @param token 验证令牌数据传输对象
	 * @return ChatResponse 对象
	 */
	@RequestMapping("/api/verify")
	public ChatResponse verify(@RequestBody VerityTokenDto token) {
		return accountService.verify(token.getEmail(), token.getToken());
	}

	/**
	 * 聊天过程。
	 *
	 * @return ChatResponse 对象
	 */
	@RequestMapping("/api/session")
	public ChatResponse session() {
		ChatResponse data = chatgptApiService.session();
		return data;
	}

}
