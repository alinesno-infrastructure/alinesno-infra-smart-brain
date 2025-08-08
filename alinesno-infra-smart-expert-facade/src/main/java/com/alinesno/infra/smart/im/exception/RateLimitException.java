package com.alinesno.infra.smart.im.exception;

import lombok.Getter;

/**
 * 自定义速率限制异常类
 * 当调用接口超过指定的频率限制时抛出此异常
 * 继承自RuntimeException，是非检查型异常，不需要强制捕获处理
 */
@Getter
public class RateLimitException extends RuntimeException {

    /**
     * -- GETTER --
     *  获取重试前需要等待的秒数
     *
     * @return 重试前需要等待的秒数
     */
    // 重试前需要等待的秒数，默认为60秒
    private int retryAfterSeconds = 60;

    /**
     * 构造函数，初始化异常信息
     *
     * @param message 异常信息，描述速率限制的细节
     */
    public RateLimitException(String message) {
        super(message);
    }

    /**
     * 构造函数，初始化异常信息和原因
     *
     * @param message 异常信息，描述速率限制的细节
     * @param cause 异常原因，通常是一个底层异常
     */
    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 设置重试前需要等待的秒数
     *
     * @param retryAfterSeconds 重试前需要等待的秒数
     */
    public void setRetryAfterSeconds(int retryAfterSeconds) {
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
