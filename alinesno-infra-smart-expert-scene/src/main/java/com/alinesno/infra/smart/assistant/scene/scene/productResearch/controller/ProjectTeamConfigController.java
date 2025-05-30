package com.alinesno.infra.smart.assistant.scene.scene.productResearch.controller;

import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTeamConfigService;
import com.alinesno.infra.smart.scene.entity.ProjectTeamConfigEntity;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "团队配置")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/productResearchTeam")
public class ProjectTeamConfigController extends BaseController<ProjectTeamConfigEntity, IProjectTeamConfigService> {
}    