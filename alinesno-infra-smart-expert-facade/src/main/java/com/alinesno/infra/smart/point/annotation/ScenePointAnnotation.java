package com.alinesno.infra.smart.point.annotation;

import com.alinesno.infra.smart.point.enums.ParamSource;

import java.lang.annotation.*;

/**
 * 场景积分操作类，针对于长任务类型，需要积分
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScenePointAnnotation {

    /**
     * 积分场景编号
     * @return
     */
    String sceneCode();

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

}