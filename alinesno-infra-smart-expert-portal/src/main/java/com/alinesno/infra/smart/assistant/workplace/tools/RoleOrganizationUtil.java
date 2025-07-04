package com.alinesno.infra.smart.assistant.workplace.tools;

import com.alinesno.infra.common.web.adapter.base.consumer.IBaseOrganizationConsumer;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.IndustryRoleOrgDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 角色与组织信息处理工具类
 */
@Component
public class RoleOrganizationUtil {

    private final IBaseOrganizationConsumer organizationConsumer;

    public RoleOrganizationUtil(IBaseOrganizationConsumer organizationConsumer) {
        this.organizationConsumer = organizationConsumer;
    }

    /**
     * 处理角色列表并关联组织信息
     * @param roles 角色列表
     * @return 包含组织信息的角色DTO列表
     */
    public List<IndustryRoleOrgDto> processRolesWithOrgInfo(List<IndustryRoleEntity> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        // 收集所有组织ID
        Set<Long> orgIds = roles.stream()
                .map(IndustryRoleEntity::getOrgId)
                .collect(Collectors.toSet());

        // 批量获取组织信息
        Map<Long, OrganizationDto> orgMap = fetchOrganizations(orgIds);

        // 组装结果DTO
        return roles.stream()
                .map(role -> buildIndustryRoleOrgDto(role, orgMap))
                .toList();
    }

    /**
     * 批量获取组织信息
     * @param orgIds 组织ID集合
     * @return 组织ID到组织DTO的映射
     */
    public Map<Long, OrganizationDto> fetchOrganizations(Set<Long> orgIds) {
        if (orgIds == null || orgIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<OrganizationDto> orgList = organizationConsumer.findOrgByIds(new ArrayList<>(orgIds)).getData();
        return orgList.stream()
                .collect(Collectors.toMap(OrganizationDto::getId, Function.identity()));
    }

    /**
     * 构建 IndustryRoleOrgDto 对象
     * @param role 角色实体
     * @param orgMap 组织信息映射
     * @return 包含组织信息的角色DTO
     */
    public IndustryRoleOrgDto buildIndustryRoleOrgDto(IndustryRoleEntity role, Map<Long, OrganizationDto> orgMap) {
        IndustryRoleOrgDto dto = new IndustryRoleOrgDto();
        BeanUtils.copyProperties(role, dto);
        dto.setOrgName(orgMap.getOrDefault(role.getOrgId(), new OrganizationDto()).getOrgName());
        return dto;
    }
}