//package com.alinesno.infra.smart.brain.service.impl;
//
//import cn.hutool.core.lang.Validator;
//import com.alibaba.fastjson.JSONObject;
//import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
//import com.alinesno.infra.smart.brain.api.reponse.ChatResponse;
//import com.alinesno.infra.smart.brain.entity.AccountEntity;
//import com.alinesno.infra.smart.brain.mapper.AccountMapper;
//import com.alinesno.infra.smart.brain.service.IAccountService;
//import com.alinesno.infra.smart.brain.utils.RandomStringGenerator;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//
//import javax.lang.exception.RpcServiceRuntimeException;
//import java.util.List;
//
///**
// * 【请填写功能名称】Service业务层处理
// *
// * @author luoxiaodong
// * @version 1.0.0
// */
//@Service
//public class AccountServiceImpl extends IBaseServiceImpl<AccountEntity, AccountMapper> implements IAccountService {
//
//	// 日志记录
//	private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
//
//	private int newUserCount = 100 ;
//
//	@Override
//	public boolean initAccount(String email , String defaultUserCount) {
//
//		// 判断数据库用户是否存在
//		List<AccountEntity> list = this.list(new QueryWrapper<AccountEntity>().eq("login_account", email));
//
//		// 初始化用户的授权码
//		String code = RandomStringGenerator.generateRandomString() ; // IdWorker.getIdStr();
//
//		// 初始化用户的信息
//		AccountEntity e = new AccountEntity();
//
//		// 配置默认初始化询问次数
//		if(defaultUserCount != null) {
//			newUserCount = Integer.parseInt(defaultUserCount) ;
//		}
//
//		if (list == null || list.isEmpty()) {
//			e.setLoginAccount(email);
//			e.setLoginPassword(code);
//
//			e.setTotalCount(newUserCount);
//		} else {
//			e = list.get(0);
//			e.setLoginPassword(code);
//		}
//
//		this.saveOrUpdate(e);
//
//		// 发送用户验证码
//		this.getVerifyCode(email, code);
//
//		return true;
//	}
//
//	/**
//	 * 获取授权码服务
//	 */
//	@Override
//	public boolean getVerifyCode(String email, String tokenCode) {
//
//		boolean isEmail = Validator.isEmail(email) ;
//		Assert.isTrue(isEmail, "邮箱格式不正确") ;
//		Assert.hasLength(email, "接收邮箱不能为空");
//
//		String appId = "";
//		String url = "";
//
////		EmailDto dto = new EmailDto();
////
////		dto.setText("您正在申请登陆智能GPT，你的授权码为【" + tokenCode + "】");
////		dto.setTitle("[授权码]登陆智能GPT登陆");
////		dto.setAppCode(appId);
////		dto.setTo(email);
////		dto.setFrom("acp_chatgpt@163.com");
////
////		String result = HttpRequest.post(url + "163Email/sendText").header("Content-Type", "application/json")// 头信息，多个头信息多次调用此方法即可
////				.body(JSONObject.toJSONString(dto)).execute().body();
////
////		log.debug("sendResult:{}", result);
//
//		return true;
//
//	}
//
//	@Override
//	public ChatResponse verify(String email, String token) {
//		String msg = "Verify successfully";
//
//		// 判断数据库用户是否存在
//		List<AccountEntity> list = this.list(new QueryWrapper<AccountEntity>().eq("login_account", email));
//
//		if(list.isEmpty()) {
//			return ChatResponse.successMsg("账户["+email+"]不存在.");
//		}
//
//		AccountEntity e = list.get(0) ;
//		String auth_secret_key = e.getLoginPassword() ;
//
//		if (StringUtils.isBlank(token)) {
//			throw new RpcServiceRuntimeException("Secret key is empty");
//		}
//
//		// 测试授权码
//		if (token.equals("admin")) {
//			return ChatResponse.successMsg(msg);
//		}
//
//		if (!auth_secret_key.equals(token)) {
//			throw new RpcServiceRuntimeException("密钥无效 | Secret key is invalid");
//		}
//
//		return ChatResponse.successMsg(msg);
//	}
//
//	@Override
//	public boolean isHasToken(String token) {
//		// 判断数据库用户是否存在
//		long count = this.count(new QueryWrapper<AccountEntity>().eq("login_password", token));
//		return count > 0;
//	}
//
//	@Override
//	public boolean validateNeedPayment(String token) {
//
//		boolean hasToken = isHasToken(token) ;
//		if(!hasToken) {
//			log.debug("token["+token+"]不存在.");
//			return true ;
//		}
//
//		// 判断数据库用户是否存在
//		AccountEntity e = this.getOne(new QueryWrapper<AccountEntity>().eq("login_password", token));
//
//		int totalCount = e.getTotalCount() ;
//		int askCount = e.getAskCount() ;
//
//		if(askCount > totalCount) {
//			log.debug("用户已经超过询问次数，总次数:{} , 已使用:{}" , totalCount , askCount);
//			return true ;
//		}
//
//		// 更新用户询问次数
//		e.setAskCount(e.getAskCount() + 1);
//		e.setTotalCount(e.getTotalCount() > 0? e.getTotalCount() - 1 : e.getTotalCount());
//		this.saveOrUpdate(e) ;
//
//		return false;
//	}
//
//	@Override
//	public ChatResponse usage(String token) {
//
//		boolean hasToken = isHasToken(token) ;
//		Assert.isTrue(hasToken , "token["+token+"]不存在.");
//
//		// 判断数据库用户是否存在
//		AccountEntity e = this.getOne(new QueryWrapper<AccountEntity>().eq("login_password", token));
//
//		JSONObject json = new JSONObject() ;
//		json.put("balance", "当前还有 " + e.getTotalCount() + " 次") ;
//		json.put("apiModel", "已咨询 " + e.getAskCount() + " 次") ;
//		json.put("timeoutMs", "120000") ;
//
//		return ChatResponse.success(json);
//	}
//
//}
