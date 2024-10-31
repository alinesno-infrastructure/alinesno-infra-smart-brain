package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.smart.assistant.adapter.BaseAuthorityConsumer;
import com.alinesno.infra.smart.assistant.adapter.dto.OrganizationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 组织管理
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/org/")
public class OrgController {

    @Autowired
    private BaseAuthorityConsumer authorityConsumer;

    /**
     * 保存组织自定义主题
     */
    @PostMapping("/updateOrgCustomTheme")
    public AjaxResult updateOrgCustomTheme(@RequestBody OrganizationDto dto) {

        long orgId = CurrentAccountJwt.get().getOrgId();

        dto.setId(orgId);
        authorityConsumer.updateOrg(dto);

        return AjaxResult.success() ;
    }

    /**
     * 创建组织 createOrg
     */
    @PostMapping("/createOrg")
    public AjaxResult createOrg(@RequestBody OrganizationDto dto) {

        authorityConsumer.createOrJoinOrg(dto , CurrentAccountJwt.getUserId());

        return AjaxResult.success() ;
    }

    /**
     * 查询用户所在组织信息
     */
    @GetMapping("/findOrg")
    public AjaxResult findOrg() {

        long orgId = CurrentAccountJwt.get().getOrgId();
        log.debug("--->>>>> = orgId = {}" , orgId);

        OrganizationDto dto = authorityConsumer.findOrg(orgId).getData() ;
        return AjaxResult.success(dto);
    }

}
