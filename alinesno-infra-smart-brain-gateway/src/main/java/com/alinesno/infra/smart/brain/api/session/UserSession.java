package com.alinesno.infra.smart.brain.api.session;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 互联网用户登陆会话
 * 
 * @author luoxiaodong
 * @version 1.0.0
 */
public class UserSession {

	private static final Logger log = LoggerFactory.getLogger(UserSession.class);

	/**
	 * 获取到用户token
	 * 
	 * @param request
	 * @return
	 */
	public static String getToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorizations");
		String token = null ; 
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			token = authorizationHeader.substring("Bearer ".length());
			log.debug("token = {}" , token);
		}
		
		return token ;
	}

}
