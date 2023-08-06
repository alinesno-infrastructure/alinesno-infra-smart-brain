package com.alineson.infra.smart.brain.service;


import com.alinesno.infra.common.facade.services.IBaseService;
import com.alineson.infra.smart.brain.api.reponse.ChatResponse;
import com.alineson.infra.smart.brain.entity.AccountEntity;

/**
 * 【请填写功能名称】Service接口
 *
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IAccountService extends IBaseService<AccountEntity> {

	/**
	 * 初始化账户信息
	 * @param email 邮箱
	 * @param defaultConfig 默认配置
	 * @return 是否成功
	 */
	boolean initAccount(String email, String defaultConfig);

	/**
	 * 获取授权码
	 * @param email 邮箱
	 * @return 是否成功
	 */
	boolean getVerifyCode(String email, String tokenCode);

	/**
	 * 验证登录用户和授权码是否正确
	 * @param email 邮箱
	 * @param token 授权码
	 * @return 聊天响应
	 */
	ChatResponse verify(String email, String token);

	/**
	 * 判断是否存在用户
	 * @param token 授权码
	 * @return 是否存在用户
	 */
	boolean isHasToken(String token);

	/**
	 * 判断是否需要支付服务
	 * @param token 授权码
	 * @return 是否需要支付服务
	 */
	boolean validateNeedPayment(String token);

	/**
	 * 获取使用次数
	 * @return 聊天响应
	 */
	ChatResponse usage(String token);
}