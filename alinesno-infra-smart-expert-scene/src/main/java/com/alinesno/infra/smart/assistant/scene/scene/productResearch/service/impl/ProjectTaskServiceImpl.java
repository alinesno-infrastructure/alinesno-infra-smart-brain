package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectManagerDTO;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.mapper.ProjectTaskMapper;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTaskService;
import com.alinesno.infra.smart.scene.entity.ProjectTaskEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Scope("prototype")
@Service
public class ProjectTaskServiceImpl extends IBaseServiceImpl<ProjectTaskEntity, ProjectTaskMapper> implements IProjectTaskService {

    @Override
    public List<ProjectTaskEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId) {

        page.setPageNum(0);
        page.setPageSize(20);

        Page<ProjectTaskEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<ProjectTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectTaskEntity::getOrgId, query.getOrgId()) ;
        wrapper.eq(ProjectTaskEntity::getSceneId, sceneId);
        wrapper.orderByDesc(ProjectTaskEntity::getAddTime) ;

        pageBean = page(pageBean, wrapper);

        return pageBean.getRecords();

    }

    @Override
    public ProjectManagerDTO getProjectDetail(Long id) {
        return null;
    }


    @Override
    public List<ProjectTaskEntity> listByStatus(String status) {
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void scheduleNextIncrementalCollect(ProjectTaskEntity project) {

    }

    @Override
    public boolean isExistGitProject(Long orgId, String gitUrl) {
        return false;
    }

    @Override
    public boolean isImportingGitProject(Long orgId, String gitUrl) {
        return false;
    }
}