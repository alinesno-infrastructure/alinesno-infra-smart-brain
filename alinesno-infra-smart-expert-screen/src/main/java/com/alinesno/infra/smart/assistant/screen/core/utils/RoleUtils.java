package com.alinesno.infra.smart.assistant.screen.core.utils;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoleUtils {

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
