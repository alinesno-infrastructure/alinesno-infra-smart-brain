package com.alineson.infra.smart.brain.api;

/**
 * 验证令牌数据传输对象。
 *
 * <p>作者：luoxiaodong</p>
 *@version 1.0.0
 */
public class VerityTokenDto {

	private String email; // 邮箱
	private String token; // 令牌

	/**
	 * 获取邮箱。
	 *
	 * @return 邮箱
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 设置邮箱。
	 *
	 * @param email 邮箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取令牌。
	 *
	 * @return 令牌
	 */
	public String getToken() {
		return token;
	}

	/**
	 * 设置令牌。
	 *
	 * @param token 令牌
	 */
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "VerityTokenDto [email=" + email + ", token=" + token + "]";
	}

}
