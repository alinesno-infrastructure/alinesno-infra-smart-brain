package com.alinesno.infra.smart.assistant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 集成一个Java开发示例工具
 * @author LuoAnDong
 * @since 2023年8月3日 上午6:23:43
 */
@Slf4j
@SpringBootApplication
public class SmartAssistantApplication {

	public static String[] args;
	public static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		SmartAssistantApplication.args = args ;
		SmartAssistantApplication.context = SpringApplication.run(SmartAssistantApplication.class, args);
	}

}