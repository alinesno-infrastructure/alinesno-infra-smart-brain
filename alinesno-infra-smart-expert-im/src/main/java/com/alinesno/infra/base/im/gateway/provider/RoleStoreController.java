package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.base.consumer.IBaseOrganizationConsumer;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.workplace.tools.RoleOrganizationUtil;
import com.alinesno.infra.smart.im.dto.IndustryRoleOrgDto;
import com.alinesno.infra.smart.im.entity.AgentStoreEntity;
import com.alinesno.infra.smart.im.enums.AgentStoreTypeEnum;
import com.alinesno.infra.smart.im.service.IAgentStoreService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 角色商店服务
 * @author luoxiaodong
 */
@Slf4j
@RestController
@Scope("prototype")
@RequestMapping("/v1/api/infra/base/im/store")
public class RoleStoreController {

    @Autowired
    private IAgentStoreService agentStoreService;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Autowired
    private RoleOrganizationUtil roleOrganizationUtil;

    @ResponseBody
    @PostMapping("/storeRoleList")
    public TableDataInfo storeRoleList(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
//        return this.toPage(model, this.getFeign(), page);
        return agentStoreService.storeRoleList(page , CurrentAccountJwt.get().getOrgId());
    }

    /**
     * 获取到所有商店类型
     * @return
     */
    @GetMapping("/getAgentStoreType")
    public AjaxResult getAgentStoreType() {
        return AjaxResult.success(AgentStoreTypeEnum.getList());
    }

//    /**
//     * 获取到所有商店里面的角色
//     */
//    @GetMapping("/getAgentStoreRole")
//    public AjaxResult getAgentStoreRole(DatatablesPageBean page) {
//
//        List<IndustryRoleEntity> publicRole = agentStoreService.findRoleFromStore(page);
//        List<IndustryRoleEntity> orgRole = agentStoreService.findRoleFromStoreByOrgId(page , CurrentAccountJwt.get().getOrgId());
//
//        // 获取角色ID列表
//        List<Long> roleIds = publicRole.stream()
//                .map(IndustryRoleEntity::getId)
//                .toList();
//
//        // 批量获取角色信息
//        List<IndustryRoleEntity> roles = industryRoleService.listByIds(roleIds);
//
//        // 收集所有组织ID
//        Set<Long> orgIds = roles.stream()
//                .map(IndustryRoleEntity::getOrgId)
//                .collect(Collectors.toSet());
//
//        // 批量获取组织信息
//        Map<Long, OrganizationDto> orgMap = new HashMap<>();
//        if(!orgIds.isEmpty()) {
//            List<OrganizationDto> orgList = organizationConsumer.findOrgByIds(new ArrayList<>(orgIds)).getData();
//            orgList.forEach(org -> orgMap.put(org.getOrgId(), org));
//        }
//
//        // 组装结果DTO
//        List<IndustryRoleOrgDto> publicRoleOrgList = roles.stream()
//                .map(role -> {
//                    IndustryRoleOrgDto dto = new IndustryRoleOrgDto();
//                    BeanUtils.copyProperties(role, dto);
//                    dto.setOrgName(orgMap.getOrDefault(role.getOrgId(), new OrganizationDto()).getOrgName());
//                    return dto;
//                })
//                .toList();
//
//        Map<String , Object> hashMap = new HashMap<>();
//        hashMap.put("publicRoleList", publicRoleOrgList);
//        hashMap.put("orgRoleList", orgRole);
//
//        return AjaxResult.success(hashMap);
//    }

    /**
     * 获取商店中的所有角色（公共角色和当前组织角色）
     */
    @GetMapping("/getAgentStoreRole")
    public AjaxResult getAgentStoreRole(DatatablesPageBean page) {
        // 并行获取公共角色和组织角色
        List<IndustryRoleEntity> publicRole = agentStoreService.findRoleFromStore(page);
        List<IndustryRoleEntity> orgRole = agentStoreService.findRoleFromStoreByOrgId(page, CurrentAccountJwt.get().getOrgId());

        // 使用工具类处理角色列表并关联组织信息
        List<IndustryRoleOrgDto> publicRoleOrgList = roleOrganizationUtil.processRolesWithOrgInfo(publicRole);
        List<IndustryRoleOrgDto> orgRoleOrgList = roleOrganizationUtil.processRolesWithOrgInfo(orgRole);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("publicRoleList", publicRoleOrgList);
        result.put("orgRoleList", orgRoleOrgList);

        return AjaxResult.success(result);
    }

}
