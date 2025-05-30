package com.alinesno.infra.smart.assistant.scene.scene.productResearch.controller;

import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectManagerService;
import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目跟进场景管理
 */
@Api(tags = "项目跟进场景管理")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/productResearchScene")
public class ProjectResearchSceneController extends BaseController<ProjectManagerEntity, IProjectManagerService> {

}    