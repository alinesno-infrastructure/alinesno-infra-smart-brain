package com.alinesno.infra.smart.assistant.point.listener.base;

import com.alinesno.infra.smart.assistant.adapter.AipPointConsumer;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.service.ISceneService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 抽象监听器类
 */
public abstract class BaseListener {

    @Autowired
    protected AipPointConsumer pointConsumer;

    @Autowired
    protected IIndustryRoleService industryRoleService;

    @Autowired
    protected ISceneService sceneService;

}
