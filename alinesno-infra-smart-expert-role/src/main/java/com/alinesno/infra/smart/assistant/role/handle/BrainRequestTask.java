package com.alinesno.infra.smart.assistant.role.handle;

import lombok.Data;
import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.Callable;

@Data
public class BrainRequestTask implements Callable<String> {

    private String url;
    private String prompt ;
    private String roleId ;

    public BrainRequestTask(String url , String prompt) {
        this.prompt = prompt ;
        this.url = url;
    }

    public BrainRequestTask(String url , String prompt , String roleId) {
        this.prompt = prompt ;
        this.url = url;
        this.roleId = roleId ;
    }

    @SneakyThrows
    @Override
    public String call() {
        // 发送HTTP请求并获取响应

        Random random = new Random();
        int randomNumber = (random.nextInt(10) + 5) * 1000; // 生成一个1到10之间的随机数
        System.out.println("随机数：" + randomNumber);

        int time = 1 ;
        Thread.sleep(randomNumber);

        // 这里使用你选择的HTTP客户端库发送请求并获取响应
        return "返回结果:" + prompt ; // 返回HTTP响应的字符串形式
    }
}