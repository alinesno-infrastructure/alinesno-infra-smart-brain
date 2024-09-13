package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.pageable.ConditionDto;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用构建Controller
 * 处理与ApplicationEntity相关的请求
 * 继承自BaseController类并实现IApplicationService接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Api(tags = "IndustryRole")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/role")
public class IndustryRoleController extends BaseController<IndustryRoleEntity, IIndustryRoleService> {

    @Autowired
    private IIndustryRoleService service;

    @Autowired
    private IIndustryRoleCatalogService catalogService ;

    /**
     * 获取ApplicationEntity的DataTables数据
     * 
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));

        List<ConditionDto> condition =  page.getConditionList() ;
        String catalogId =  request.getParameter("industryCatalog") ;

        if(StringUtils.isNotBlank(catalogId)){
            ConditionDto dto = new ConditionDto() ;

            dto.setColumn("industry_catalog");
            dto.setValue(catalogId);
            dto.setType("eq");

            condition.add(dto) ;
            page.setConditionList(condition);
        }

        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 运行角色流程
     * @return
     */
    @GetMapping("/listAllRole")
    public AjaxResult listAllRole(){
        LambdaQueryWrapper<IndustryRoleEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.orderByDesc(IndustryRoleEntity::getAddTime) ;
        List<IndustryRoleEntity> roleEntityList = service.list(wrapper) ;
        return AjaxResult.success(roleEntityList) ;
    }

    /**
     *
     * @return
     */
    @PostMapping("/updatePromptContent")
    public AjaxResult updatePromptContent(@RequestBody List<PromptMessageDto> messageDto , String roleId){
        service.updatePromptContent(messageDto , roleId) ;
        return ok() ;
    }

    @GetMapping("/catalogTreeSelect")
    public AjaxResult catalogTreeSelect(){
        return AjaxResult.success("success" , catalogService.selectCatalogTreeList()) ;
    }

    @Override
    public IIndustryRoleService getFeign() {
        return this.service;
    }
}