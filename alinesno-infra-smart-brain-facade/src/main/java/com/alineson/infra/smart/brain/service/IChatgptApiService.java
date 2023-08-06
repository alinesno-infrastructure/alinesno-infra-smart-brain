package com.alineson.infra.smart.brain.service;

import com.alineson.infra.smart.brain.api.reponse.ChatResponse;
import com.alineson.infra.smart.brain.client.ChatGPTOpenAiStreamClient;

/**
 * 聊天GPT API服务接口
 *
 * @version 1.0.0
 * @since 2023年3月25日 上午6:23:43
 * @author luoxiaodong
 */
public interface IChatgptApiService {

	/**
	 * 获取Session
	 *
	 * @return 聊天响应
	 */
	ChatResponse session();

	/**
	 * 聊天
	 *
	 * @return 聊天响应
	 */
	ChatResponse chat();

	/**
	 * 获取配置
	 *
	 * @return 聊天响应
	 */
	ChatResponse config();

	/**
	 * 处理聊天
	 *
	 * @return 聊天响应
	 */
	ChatResponse chatProcess();

	/**
	 * 验证
	 *
	 * @param email 邮箱
	 * @param token 令牌
	 * @return 聊天响应
	 */
	ChatResponse verify(String email, String token);

	/**
	 * 获取聊天GPT OpenAi流客户端
	 *
	 * @return 聊天GPT OpenAi流客户端
	 */
	ChatGPTOpenAiStreamClient getClient();

}