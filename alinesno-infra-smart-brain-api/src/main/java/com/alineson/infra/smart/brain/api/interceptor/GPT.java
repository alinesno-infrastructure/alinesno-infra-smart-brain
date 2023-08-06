package com.alineson.infra.smart.brain.api.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标记需要使用 GPT 拦截器的方法。
 *
 * <p>作者：luoxiaodong</p>
 *@version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GPT {

    // 可以根据需要定义任何属性或方法
}