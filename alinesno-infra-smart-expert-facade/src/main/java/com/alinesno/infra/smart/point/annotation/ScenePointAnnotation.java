package com.alinesno.infra.smart.point.annotation;

import java.lang.annotation.*;

/**
 * 场景积分操作类，针对于长任务类型，需要积分
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScenePointAnnotation {
    String sceneCode(); // 场景编号
}