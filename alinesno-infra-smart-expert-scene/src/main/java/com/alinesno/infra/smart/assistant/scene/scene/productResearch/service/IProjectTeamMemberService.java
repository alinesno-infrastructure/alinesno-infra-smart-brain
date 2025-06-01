package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
import com.alinesno.infra.smart.scene.entity.ProjectTeamMemberEntity;

import java.util.List;

public interface IProjectTeamMemberService extends IBaseService<ProjectTeamMemberEntity> {

    /**
     * 更新组织团队信息
     *
     * @param teams
     * @return
     */
    List<ProjectTeamMemberEntity> updateOrgTeam(List<ProjectTeamMemberEntity> teams, ProjectManagerEntity project);
}