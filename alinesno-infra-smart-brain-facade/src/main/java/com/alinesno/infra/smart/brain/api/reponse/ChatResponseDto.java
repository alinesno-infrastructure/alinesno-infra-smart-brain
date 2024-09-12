package com.alinesno.infra.smart.brain.api.reponse;

import com.google.gson.Gson;
import lombok.Getter;

/**
 * DTO（Data Transfer Object）用于封装聊天响应的数据。
 * @author luoxiaodong
 * @version 1.0.0
 */
@Getter
public class ChatResponseDto {

	/**
	 * -- GETTER --
	 *  获取角色信息。
	 *
	 * @return 角色信息。
	 */
	private String role;
	/**
	 * -- GETTER --
	 *  获取消息ID。
	 *
	 * @return 消息ID。
	 */
	private String id;
	/**
	 * -- GETTER --
	 *  获取父消息ID。
	 *
	 * @return 父消息ID。
	 */
	private String parentMessageId;
	/**
	 * -- GETTER --
	 *  获取文本内容。
	 *
	 * @return 文本内容。
	 */
	private String text;
	/**
	 * -- GETTER --
	 *  获取详细信息。
	 *
	 * @return 详细信息。
	 */
	private Object detail;

	/**
	 * 设置角色信息。
	 *
	 * @param role 角色信息。
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * 设置消息ID。
	 *
	 * @param id 消息ID。
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置父消息ID。
	 *
	 * @param parentMessageId 父消息ID。
	 */
	public void setParentMessageId(String parentMessageId) {
		this.parentMessageId = parentMessageId;
	}

	/**
	 * 设置文本内容。
	 *
	 * @param text 文本内容。
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 设置详细信息。
	 *
	 * @param detail 详细信息。
	 */
	public void setDetail(Object detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}