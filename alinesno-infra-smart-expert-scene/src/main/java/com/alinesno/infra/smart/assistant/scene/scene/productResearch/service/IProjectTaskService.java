package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectManagerDTO;
import com.alinesno.infra.smart.scene.entity.ProjectTaskEntity;

import java.util.List;

public interface IProjectTaskService extends IBaseService<ProjectTaskEntity> {

    /**
     * 分页查询
     * @param page
     * @param query
     * @return
     */
    List<ProjectTaskEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId);

    /**
     * 获取项目详情
     * @param id
     * @return
     */
    ProjectManagerDTO getProjectDetail(Long id);

//    /**
//     * 导入Git项目
//     * @param gitInfoDto
//     * @return
//     */
//    ProjectManagerDTO importGitProject(GitInfoDto gitInfoDto);

    /**
     * listByStatus
     */
    List<ProjectTaskEntity> listByStatus(String status);

    /**
     * 销毁采集线程池
     */
    void destroy();

    /**
     * 下次增量采集
     * @param project
     */
    void scheduleNextIncrementalCollect(ProjectTaskEntity project);

    /**
     * 判断是否存在同仓库
     * @param orgId
     * @param gitUrl
     * @return
     */
    boolean isExistGitProject(Long orgId, String gitUrl);

    /**
     * 判断是否正在采集
     * @param orgId
     * @param gitUrl
     * @return
     */
    boolean isImportingGitProject(Long orgId, String gitUrl);
}