package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.smart.assistant.api.IndustryFrequentRoleDto;
import com.alinesno.infra.smart.assistant.workplace.dto.OrgWorkplaceDto;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceImResponseDto;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceResponseDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceEntity;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceItemTypeEnums;
import com.alinesno.infra.smart.assistant.workplace.service.IOrgWorkplaceService;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceItemService;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceService;
import com.alinesno.infra.smart.im.dto.CollectItemDto;
import com.alinesno.infra.smart.im.dto.CollectItemObjectDto;
import com.alinesno.infra.smart.im.dto.CustomizeWorkbenchDTO;
import com.alinesno.infra.smart.im.dto.HomePageDto;
import com.alinesno.infra.smart.im.service.IAccountHomePageService;
import com.alinesno.infra.smart.im.service.IFrequentAgentService;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 工作台配置管理
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/api/infra/base/im/workplace/")
public class ImWorkplaceController {

    @Autowired
    private IWorkplaceService workplaceService;

    @Autowired
    private IOrgWorkplaceService orgWorkplaceService;

    @Autowired
    private IWorkplaceItemService workplaceItemService;

    @Autowired
    private IAccountHomePageService accountHomePageService;

    @Autowired
    private IFrequentAgentService frequentAgentService;

    /**
     * 获取最近访问列表getFrequentItem
     */
    @GetMapping("/getFrequentItem")
    public AjaxResult getFrequentItem() {
        List<IndustryFrequentRoleDto> list = frequentAgentService.getFrequentAgentList(CurrentAccountJwt.getUserId() , 10);
        return AjaxResult.success("操作成功." , list);
    }

    /**
     * 设置主页setHomePage
     */
    @PostMapping("/setHomePage")
    public AjaxResult setHomePage(@RequestBody @Validated HomePageDto dto) {
        log.debug("dto = {}", dto);

        long accountId = CurrentAccountJwt.getUserId() ;
        String homePage = dto.getHomePage() ;
        String type = dto.getType() ;

        boolean b = accountHomePageService.setHomePage(accountId , homePage , type) ;

        return b?AjaxResult.success() : AjaxResult.error() ;
    }

    /**
     * 获取当前用户主页
     */
    @GetMapping("/getHomePage")
    public AjaxResult getHomePage(){
        long accountId = CurrentAccountJwt.getUserId() ;
        return AjaxResult.success(accountHomePageService.getByAccountId(accountId)) ;
    }

    /**
     * 是否有工作台
     * @return
     */
    @GetMapping("/isHasWorkplace")
    public AjaxResult isHasWorkplace() {
        Long workplaceId = orgWorkplaceService.isHasWorkplace(CurrentAccountJwt.get().getOrgId()) ;
        return AjaxResult.success("操作成功." , workplaceId);
    }

    /**
     * 是否有个人工作台
     * @return
     */
    @GetMapping("/isHasAccountWorkplace")
    public AjaxResult isHasAccountWorkplace() {
        Long workplaceId = orgWorkplaceService.isHasAccountWorkplace(CurrentAccountJwt.get().getId() , CurrentAccountJwt.get().getOrgId()) ;
        return AjaxResult.success("操作成功." , workplaceId);
    }

    /**
     * 获取当前工作台
     * @return
     */
    @GetMapping("/getCurrentWorkplace")
    public AjaxResult getCurrentWorkplace(@RequestParam(value = "workplaceId") Long workplaceId , String type) {

        WorkplaceEntity entity = workplaceService.getById(workplaceId) ;

        WorkplaceResponseDto dto = new WorkplaceResponseDto() ;
        BeanUtils.copyProperties(entity , dto) ;

        // 查询出工作台下的所有场景列表
        List<CollectItemObjectDto> workplaceItems = workplaceItemService.getWorkplaceItemByType(workplaceId , type) ;

        AjaxResult result = AjaxResult.success("操作成功." , dto);
        result.put("workplaceItems" , workplaceItems);

        return result  ;
    }

    /**
     * 收藏事项collectItem
     */
    @PostMapping("/collectItem")
    public AjaxResult collectItem(@RequestBody @Validated CollectItemDto dto) {
        log.debug("dto = {}", dto);

        Long workplaceId = orgWorkplaceService.isHasAccountWorkplace(CurrentAccountJwt.get().getId() , CurrentAccountJwt.get().getOrgId()) ;
        workplaceItemService.addCollectItem(dto , workplaceId) ;

        return AjaxResult.success() ;
    }

    /**
     * 收藏事项collectItem
     */
    @PostMapping("/unCollectItem")
    public AjaxResult unCollectItem(@RequestBody @Validated CollectItemDto dto) {
        log.debug("dto = {}", dto);

        Long workplaceId = orgWorkplaceService.isHasAccountWorkplace(CurrentAccountJwt.get().getId() , CurrentAccountJwt.get().getOrgId()) ;
        workplaceItemService.removeCollectItem(dto , workplaceId) ;

        return AjaxResult.success() ;
    }

    /**
     * 获取组织工作台，组织工作台为公共工作台和组织当前工作台
     * @return
     */
    @GetMapping("/listOrgWorkplace")
    public AjaxResult listOrgWorkplace() {
        List<WorkplaceEntity> list =  workplaceService.listOrgWorkplace(CurrentAccountJwt.get().getOrgId()) ;
        return AjaxResult.success(list);
    }

    /**
     * 使用用户工作台
     * @return
     */
    @DataPermissionSave
    @PostMapping("/useWorkplace")
    public AjaxResult useWorkplace(@RequestBody OrgWorkplaceDto dto) {
        orgWorkplaceService.useWorkplace(dto) ;
        return AjaxResult.success();
    }

    /**
     * 自定义工作台constomWorkplace
     */
    @DataPermissionSave
    @PostMapping("/customWorkplace")
    public AjaxResult customWorkplace(@RequestBody CustomizeWorkbenchDTO dto) {
        orgWorkplaceService.customizeWorkbench(dto) ;
        return AjaxResult.success();
    }

}
