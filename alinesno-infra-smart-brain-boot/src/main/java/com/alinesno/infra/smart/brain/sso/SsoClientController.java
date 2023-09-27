package com.alinesno.infra.plat.console.sso;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.dtflys.forest.Forest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 集成单点登陆认证信息
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@RestController
public class SsoClientController {

	/*
	 * SSO-Client端：处理所有SSO相关请求
	 * 		http://{host}:{port}/sso/login			-- Client端登录地址，接受参数：back=登录后的跳转地址
	 * 		http://{host}:{port}/sso/logout			-- Client端单点注销地址（isSlo=true时打开），接受参数：back=注销后的跳转地址
	 * 		http://{host}:{port}/sso/logoutCall		-- Client端单点注销回调地址（isSlo=true时打开），此接口为框架回调，开发者无需关心
	 */
	@GetMapping("/sso/*")
	public Object ssoRequestForGet() {
		return SaSsoProcessor.instance.clientDister();
	}

	@PostMapping("/sso/*")
	public Object ssoRequestForPost() {
		return SaSsoProcessor.instance.clientDister();
	}

	@PutMapping("/sso/*")
	public Object ssoRequestForPut() {
		return SaSsoProcessor.instance.clientDister();
	}

	@DeleteMapping("/sso/*")
	public Object ssoRequestForDelete() {
		return SaSsoProcessor.instance.clientDister();
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

	// 查询我的账号信息
	@GetMapping("/sso/myInfo")
	public Object myInfo() {
		// 组织请求参数
		Map<String, Object> map = new HashMap<>();
		map.put("apiType", "userinfo");
		map.put("loginId", StpUtil.getLoginId());

		// 发起请求
		Object resData = SaSsoUtil.getData(map);
		System.out.println("sso-server 返回的信息：" + resData);
		return resData;
	}

}