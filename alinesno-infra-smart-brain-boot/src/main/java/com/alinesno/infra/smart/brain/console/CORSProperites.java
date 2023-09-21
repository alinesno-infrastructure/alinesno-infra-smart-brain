package com.alinesno.infra.smart.brain.console;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 跨域配置
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "alinesno.cors")
public class CORSProperites {

	private List<String> allowedOrigins;
	private boolean allowCredentials;
	private String allowMethods;
	private String allowHeaders;

	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(List<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	public boolean isAllowCredentials() {
		return allowCredentials;
	}

	public void setAllowCredentials(boolean allowCredentials) {
		this.allowCredentials = allowCredentials;
	}

	public String getAllowMethods() {
		return allowMethods;
	}

	public void setAllowMethods(String allowMethods) {
		this.allowMethods = allowMethods;
	}

	public String getAllowHeaders() {
		return allowHeaders;
	}

	public void setAllowHeaders(String allowHeaders) {
		this.allowHeaders = allowHeaders;
	}

}
