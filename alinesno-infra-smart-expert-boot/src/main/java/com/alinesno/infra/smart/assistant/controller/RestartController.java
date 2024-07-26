package com.alinesno.infra.smart.assistant.controller;

import com.alinesno.infra.smart.assistant.SmartAssistantApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
 
/**
 * 重新启动项目
 */
@Slf4j
@RestController
public class RestartController {

    @Value("${spring.application.name}")
    String name;

    @RequestMapping("/refresh")
    public String restart() {
        ExecutorService threadPool = new ThreadPoolExecutor(1, 1, 0,  TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        log.debug("应用重新启动.");
        threadPool.execute(() -> {
            SmartAssistantApplication.context.close();
            SmartAssistantApplication.context = SpringApplication.run(SmartAssistantApplication.class, SmartAssistantApplication.args);
        });
        threadPool.shutdown();
        return "spring.application.name:" + name;
    }
 
    @RequestMapping("/info")
    public String info() {
        log.info("spring.application.name:" + name);
        return name;
    }
}