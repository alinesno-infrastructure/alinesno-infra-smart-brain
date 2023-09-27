package com.alinesno.infra.plat.console.sso;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.UUID;

/**
 * 集成Sa-Token前后端分离单点登陆代码
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@RestController
public class H5Controller {

	// 当前是否登录 
	@GetMapping("/sso/isLogin")
	public Object isLogin() {
		return SaResult.data(StpUtil.isLogin());
	}
	
	// 返回SSO认证中心登录地址 
	@GetMapping("/sso/getSsoAuthUrl")
	public SaResult getSsoAuthUrl(String clientLoginUrl) {
		String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(clientLoginUrl, "");
		return SaResult.data(serverAuthUrl);
	}
	
	// 根据ticket进行登录 
	@GetMapping("/sso/doLoginByTicket")
	public SaResult doLoginByTicket(String ticket) {
		Object loginId = SaSsoProcessor.instance.checkTicket(ticket, "/sso/doLoginByTicket");
		if(loginId != null) {
			StpUtil.login(loginId);

			String adminToken = UUID.randomUUID().toString() ;

			SaResult result = SaResult.data(StpUtil.getTokenValue());
			result.put("adminToken" , adminToken) ;

			return result ;
		}
		return SaResult.error("无效ticket：" + ticket); 
	}

	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		return SaResult.error(e.getMessage());
	}
	
}
