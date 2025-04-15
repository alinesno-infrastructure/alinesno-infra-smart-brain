package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.enums.AgentStoreTypeEnum;
import com.alinesno.infra.smart.im.service.IAgentStoreService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取到所有商店里面的角色
     */
    @GetMapping("/getAgentStoreRole")
    public AjaxResult getAgentStoreRole(DatatablesPageBean page) {

        List<IndustryRoleEntity> publicRole = agentStoreService.findRoleFromStore(page);
        List<IndustryRoleEntity> orgRole = agentStoreService.findRoleFromStoreByOrgId(page , CurrentAccountJwt.get().getOrgId());

        // TODO 获取到组织信息，将两个角色进行合并
        List<Long> publicIds = publicRole.stream().map(IndustryRoleEntity::getOrgId).distinct().toList();
        List<Long> orgIds = orgRole.stream().map(IndustryRoleEntity::getOrgId).distinct().toList();

        Map<String , Object> hashMap = new HashMap<>();
        hashMap.put("publicRoleList", publicRole);
        hashMap.put("orgRoleList", orgRole);

        return AjaxResult.success(hashMap);
    }

}
