package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.mapper.ProjectTeamMemberMapper;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTeamMemberService;
import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
import com.alinesno.infra.smart.scene.entity.ProjectTeamMemberEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectTeamMemberServiceImpl extends IBaseServiceImpl<ProjectTeamMemberEntity, ProjectTeamMemberMapper> implements IProjectTeamMemberService {

    @Override
    public List<ProjectTeamMemberEntity> updateOrgTeam(List<ProjectTeamMemberEntity> teams, ProjectManagerEntity project) {

        // 使用Stream根据email去重，保留第一次出现的记录
        List<ProjectTeamMemberEntity> newTeams =  teams.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                ProjectTeamMemberEntity::getUserEmail,
                                Function.identity(),
                                (existing, replacement) -> existing // 保留先出现的记录
                        ),
                        map -> new ArrayList<>(map.values())
                ));
        // 2. 处理过滤后的团队成员
        newTeams.forEach(team -> {
            LambdaQueryWrapper<ProjectTeamMemberEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProjectTeamMemberEntity::getOrgId, project.getOrgId());
            wrapper.eq(ProjectTeamMemberEntity::getUserEmail, team.getUserEmail());

            ProjectTeamMemberEntity existingTeam = getOne(wrapper);

            if (existingTeam == null) {
                // 设置组织ID和项目ID
                team.setOrgId(project.getOrgId());
                save(team);
                log.debug("新增团队成员: {}", team.getUserEmail());
            } else {
                // 更新现有记录
                team.setId(existingTeam.getId());
                updateById(team);
                log.debug("更新团队成员: {}", team.getUserEmail());
            }
        });

        log.info("完成团队成员更新，组织ID: {}, 处理记录数: {}", project.getOrgId(), newTeams.size());

        return teams ;

    }
}