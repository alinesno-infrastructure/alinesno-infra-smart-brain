package com.alinesno.infra.smart.assistant.scene.scene.productResearch.controller;

import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectGitCommitService;
import com.alinesno.infra.smart.scene.entity.ProjectGitCommitEntity;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Git提交信息")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/productResearchCommit")
public class ProjectGitCommitController extends BaseController<ProjectGitCommitEntity, IProjectGitCommitService> {
}    