package com.alinesno.infra.smart.brain.api.dto;

import com.alinesno.infra.smart.brain.api.reponse.ChatResponse;

/**
 * DTO（Data Transfer Object）用于封装聊天请求的数据。
 * @author luoxiaodong
 * @version 1.0.0
 */
public class ChatRequestDto {

	private String prompt;
	private ChatResponse options;
	private String systemMessage;

	/**
	 * 获取聊天请求的提示信息。
	 *
	 * @return 聊天请求的提示信息。
	 */
	public String getPrompt() {
		return prompt;
	}

	/**
	 * 设置聊天请求的提示信息。
	 *
	 * @param prompt 聊天请求的提示信息。
	 */
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	/**
	 * 获取聊天请求的选项。
	 *
	 * @return 聊天请求的选项。
	 */
	public ChatResponse getOptions() {
		return options;
	}

	/**
	 * 设置聊天请求的选项。
	 *
	 * @param options 聊天请求的选项。
	 */
	public void setOptions(ChatResponse options) {
		this.options = options;
	}

	/**
	 * 获取聊天请求的系统消息。
	 *
	 * @return 聊天请求的系统消息。
	 */
	public String getSystemMessage() {
		return systemMessage;
	}

	/**
	 * 设置聊天请求的系统消息。
	 *
	 * @param systemMessage 聊天请求的系统消息。
	 */
	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}

	@Override
	public String toString() {
		return "ChatRequestDto [prompt=" + prompt + ", options=" + options + ", systemMessage=" + systemMessage + "]";
	}
}
