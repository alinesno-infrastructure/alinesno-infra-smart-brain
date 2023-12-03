package com.alinesno.infra.smart.brain.service;

import com.alinesno.infra.smart.brain.client.OpenAiClient;

/**
 * 聊天GPT API服务接口
 *
 * @version 1.0.0
 * @since 2023年3月25日 上午6:23:43
 * @author luoxiaodong
 */
public interface ILLMApiService {

	/**
	 * 获取聊天GPT OpenAi流客户端
	 *
	 * @return 聊天GPT OpenAi流客户端
	 */
	OpenAiClient getClient();

}