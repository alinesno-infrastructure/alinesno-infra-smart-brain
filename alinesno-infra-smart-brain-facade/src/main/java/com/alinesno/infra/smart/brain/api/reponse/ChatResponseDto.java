package com.alinesno.infra.smart.brain.api.reponse;

import com.google.gson.Gson;

/**
 * DTO（Data Transfer Object）用于封装聊天响应的数据。
 * @author luoxiaodong
 * @version 1.0.0
 */
public class ChatResponseDto {

	private String role;
	private String id;
	private String parentMessageId;
	private String text;
	private Object detail;

	/**
	 * 获取角色信息。
	 *
	 * @return 角色信息。
	 */
	public String getRole() {
		return role;
	}

	/**
	 * 设置角色信息。
	 *
	 * @param role 角色信息。
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * 获取消息ID。
	 *
	 * @return 消息ID。
	 */
	public String getId() {
		return id;
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
	 * 获取父消息ID。
	 *
	 * @return 父消息ID。
	 */
	public String getParentMessageId() {
		return parentMessageId;
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
	 * 获取文本内容。
	 *
	 * @return 文本内容。
	 */
	public String getText() {
		return text;
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
	 * 获取详细信息。
	 *
	 * @return 详细信息。
	 */
	public Object getDetail() {
		return detail;
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