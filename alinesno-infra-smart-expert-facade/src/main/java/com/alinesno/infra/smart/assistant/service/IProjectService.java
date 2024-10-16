package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.ProjectEntity;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface IProjectService extends IBaseService<ProjectEntity> {

    /**
     * 初始化默认应用
     * @param userId
     */
    void initDefaultApp(long userId);

    ProjectEntity getProjectByAccountId(long userId);
}