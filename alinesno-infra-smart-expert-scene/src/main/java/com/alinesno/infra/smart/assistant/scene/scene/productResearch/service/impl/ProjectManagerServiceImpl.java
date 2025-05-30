package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.mapper.ProjectManagerMapper;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectManagerService;
import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProjectManagerServiceImpl extends IBaseServiceImpl<ProjectManagerEntity , ProjectManagerMapper> implements IProjectManagerService {
}    