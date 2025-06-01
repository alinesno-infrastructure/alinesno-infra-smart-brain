package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectManagerDTO;
import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;

import java.util.List;

public interface IProjectManagerService extends IBaseService<ProjectManagerEntity> {

    /**
     * 分页查询
     * @param page
     * @param query
     * @return
     */
    List<ProjectManagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query);

    /**
     * 获取项目详情
     * @param id
     * @return
     */
    ProjectManagerDTO getProjectDetail(Long id);

    /**
     * 导入Git项目
     * @param gitInfoDto
     * @return
     */
    ProjectManagerDTO importGitProject(GitInfoDto gitInfoDto);

    /**
     * listByStatus
     */
    List<ProjectManagerEntity> listByStatus(String status);

    /**
     * 销毁采集线程池
     */
    void destroy();

    /**
     * 下次增量采集
     * @param project
     */
    void scheduleNextIncrementalCollect(ProjectManagerEntity project);

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