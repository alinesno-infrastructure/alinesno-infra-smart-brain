package com.alinesno.infra.smart.assistant.workplace.tools;

import com.alinesno.infra.common.web.adapter.base.consumer.IBaseOrganizationConsumer;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.dto.IndustryRoleOrgDto;
import com.alinesno.infra.smart.im.dto.RoleFeedbackStat;
import com.alinesno.infra.smart.im.service.IMessageFeedbackService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IMessageFeedbackService messageFeedbackService;

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

        // 收集所有角色ID
        Set<Long> roleIds = roles.stream()
                .map(IndustryRoleEntity::getId)
                .collect(Collectors.toSet());

        // 批量获取组织信息
        Map<Long, OrganizationDto> orgMap = fetchOrganizations(orgIds);

        // 获取角色反馈统计
        Map<Long, FeedbackStats> feedbackStatsMap = getFeedbackStatisticsByRoleIds(roleIds);

        // 组装结果DTO
        return roles.stream()
                .map(role -> {
                    IndustryRoleOrgDto dto = buildIndustryRoleOrgDto(role, orgMap);
                    FeedbackStats stats = feedbackStatsMap.getOrDefault(role.getId(), new FeedbackStats());
                    dto.setLike(stats.like);
                    dto.setDislike(stats.dislike);
                    return dto;
                })
                .toList();
    }

    /**
     * 反馈统计内部类
     */
    private static class FeedbackStats {
        int like;
        int dislike;
    }

    private Map<Long, FeedbackStats> getFeedbackStatisticsByRoleIds(Set<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 直接查询数据库获取统计结果（优化后的 SQL）
        List<RoleFeedbackStat> stats = messageFeedbackService.countFeedbackByRoleIds(
                new ArrayList<>(roleIds)
        );

        // 转换为 Map<roleId, FeedbackStats>
        return stats.stream()
                .collect(Collectors.toMap(
                        RoleFeedbackStat::getRoleId,
                        stat -> {
                            FeedbackStats fs = new FeedbackStats();
                            fs.like = stat.getLikeCount();
                            fs.dislike = stat.getDislikeCount();
                            return fs;
                        }
                ));
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