package com.alinesno.infra.smart.point.annotation;

import com.alinesno.infra.smart.point.enums.ParamSource;

import java.lang.annotation.*;

/**
 * 智能体积分注解，针对于智能体聊天类型
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgentPointAnnotation {

    /**
     * 智能体编号
     * @return
     */
    String agentCode() default  "" ;

    /**
     * roleId参数获取形式(params从url中获取/body则从对象里面获取)、默认名称为roleId
     *
     * @return
     */
    ParamSource paramType() default ParamSource.PARAMS;

    /**
     * 获取参数名称
     * @return
     */
    String paramName() default "roleId";

    /**
     *（每秒允许的请求数）
     * @return
     */
    double rateLimit() default -1;

}