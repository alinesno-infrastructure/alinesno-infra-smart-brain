package com.alinesno.infra.smart.brain.sso;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.dtflys.forest.Forest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

	// 配置SSO相关参数
	@Autowired
	private void configSso(SaSsoConfig sso) {
		// 配置Http请求处理器
		sso.setSendHttp(url -> {
			System.out.println("------ 发起请求：" + url);
			return Forest.get(url).executeAsString();
		});
	}

	// 全局异常拦截
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		return SaResult.error(e.getMessage());
	}

}