package com.alinesno.infra.smart.assistant.point.annotation;

/**
 * 场景积分操作类，针对于长任务类型，需要积分
 */
public @interface ScenePointAnnotation {
    String sceneCode = null; // 场景编号
}
