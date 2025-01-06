package com.alinesno.infra.smart.assistant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 集成一个Java开发示例工具
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
public class SmartAssistantApplication {

	public static String[] args;
	public static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(SmartAssistantApplication.class, args);
	}

}