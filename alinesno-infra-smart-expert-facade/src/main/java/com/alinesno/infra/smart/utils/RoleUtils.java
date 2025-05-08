package com.alinesno.infra.smart.utils;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoleUtils {

    public static long findSelectAgentIdByCode(UpdateSceneAgentDto dto, String code) {
        for (UpdateSceneAgentDto.AgentDto agent : dto.getAgents()) {
            if (agent.getCode().equals(code)) {
                return agent.getSelectAgentId();
            }
        }
        return -1; // 如果未找到，返回 -1
    }

    // 查询编辑人员的公共方法
     public static List<IndustryRoleDto> getEditors(IIndustryRoleService roleService , String editorString) {
        if (editorString == null) {
            return Collections.emptyList();
        }
        try {
            String[] split = editorString.split(",");
            List<Long> ids = Arrays.stream(split).map(s -> {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("无效的编辑人员ID: " + s, e);
                }
            }).toList();
            List<IndustryRoleEntity> roles = roleService.listByIds(ids);

            return roles.stream().map(role -> {
                IndustryRoleDto dto1 = new IndustryRoleDto();
                BeanUtils.copyProperties(role, dto1);
                return dto1;
            }).toList();
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }

    public static List<IndustryRoleEntity> getRoleEntity(IIndustryRoleService roleService , String editorString) {
        if (editorString == null) {
            return Collections.emptyList();
        }
        try {
            String[] split = editorString.split(",");
            List<Long> ids = Arrays.stream(split).map(s -> {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("无效的编辑人员ID: " + s, e);
                }
            }).toList();

            return roleService.listByIds(ids);
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }

}
