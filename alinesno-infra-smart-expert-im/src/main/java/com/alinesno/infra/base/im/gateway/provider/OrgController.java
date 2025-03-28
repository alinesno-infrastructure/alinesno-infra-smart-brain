package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.base.consumer.IBaseOrganizationConsumer;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.smart.assistant.api.GlobalConfigDTO;
import com.alinesno.infra.smart.assistant.entity.GlobalConfigEntity;
import com.alinesno.infra.smart.assistant.service.IGlobalConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private IBaseOrganizationConsumer baseOrgConsumer ;

    @Autowired
    private IGlobalConfigService service;

    /**
     * 保存组织自定义主题
     */
    @PostMapping("/updateOrgCustomTheme")
    public AjaxResult updateOrgCustomTheme(@RequestBody OrganizationDto dto) {

        long orgId = CurrentAccountJwt.get().getOrgId();

        dto.setId(orgId);
        baseOrgConsumer.updateOrg(dto);

        return AjaxResult.success() ;
    }

    /**
     * 创建组织 createOrg
     */
    @PostMapping("/createOrg")
    public AjaxResult createOrg(@RequestBody OrganizationDto dto) {

        baseOrgConsumer.createOrJoinOrg(dto , CurrentAccountJwt.getUserId());

        return AjaxResult.success() ;
    }

    /**
     * 查询用户所在组织信息
     */
    @GetMapping("/findOrg")
    public AjaxResult findOrg() {

        long orgId = CurrentAccountJwt.get().getOrgId();
        log.debug("--->>>>> = orgId = {}" , orgId);

        OrganizationDto dto = baseOrgConsumer.findOrg(orgId).getData() ;
        return AjaxResult.success(dto);
    }

    /**
     * 根据组织 ID 获取全局配置信息
     * @return 包含全局配置信息的 AjaxResult
     */
    @GetMapping("/getByOrg")
    public AjaxResult getByOrganizationId() {
        GlobalConfigEntity entity = service.getByOrganizationId(CurrentAccountJwt.get().getOrgId());
        if (entity == null) {
            return AjaxResult.success(GlobalConfigDTO.defaultDto());
        }
        GlobalConfigDTO dto = new GlobalConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return AjaxResult.success(dto);
    }
}
