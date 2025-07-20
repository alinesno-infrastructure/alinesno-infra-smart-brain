package com.alinesno.infra.smart.assistant.gateway.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.ConditionDto;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.base.consumer.IBaseOrganizationConsumer;
import com.alinesno.infra.common.web.adapter.base.dto.ManagerAccountDto;
import com.alinesno.infra.common.web.adapter.dto.FieldDto;
import com.alinesno.infra.common.web.adapter.login.account.CurrentAccountJwt;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.*;
import com.alinesno.infra.smart.assistant.api.config.RoleDeepSearchConfigDto;
import com.alinesno.infra.smart.assistant.api.config.RoleFlowConfigDto;
import com.alinesno.infra.smart.assistant.api.config.RoleReActConfigDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleCatalogEntity;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IRolePushOrgService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.enums.PushOptions;
import com.alinesno.infra.smart.im.service.IAgentStoreService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    private CloudStorageConsumer cloudStorageConsumer ;

    @Autowired
    private IBaseOrganizationConsumer baseOrgConsumer; ;

    @Autowired
    private IIndustryRoleService service;

    @Autowired
    private IRolePushOrgService rolePushOrgService ;

    @Autowired
    private IIndustryRoleCatalogService catalogService ;

    @Autowired
    private IAgentStoreService agentStoreService ;

    /**
     * 获取ApplicationEntity的DataTables数据
     * 
     * @param request HttpServletRequest对象
     * @param model Model对象
     * @param page DatatablesPageBean对象
     * @return 包含DataTables数据的TableDataInfo对象
     */
    @DataPermissionScope
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
     * 通过组织号查询组织
     */
    @GetMapping(value = "/findOrg" )
    public AjaxResult findOrg(@RequestParam String orgId){

        Assert.notNull(orgId, "组织参数为空");
        Assert.isTrue(NumberUtil.isNumber(orgId), "组织参数错误");

        return AjaxResult.success(baseOrgConsumer.findOrg(Long.parseLong(orgId)).getData()) ;
    }

    /**
     * 推送到指定的组织号confirmPushOrg
     */
    @PostMapping("/confirmPushOrg")
    public AjaxResult confirmPushOrg(@RequestBody @Validated PushOrgDto dto) {

        long accountOrgId = CurrentAccountJwt.get().getOrgId();

        // 推送组织不能为当前组织
        Assert.isTrue(accountOrgId != dto.getOrgId(), "推送组织不能为当前组织");

        if(PushOptions.MARKET.getValue().equals(dto.getPushType())){  // 推送到市场
            rolePushOrgService.pushOrgRole(dto.getRoleId(), dto.getOrgId());
        }else if(PushOptions.SHOP.getValue().equals(dto.getPushType())){  // 推送到商店
            agentStoreService.pushOrgRole(dto.getRoleId(), dto.getOrgId());
        }

        return ok() ;
    }

    /**
     * 获取用户信息和工具getRoleWithTool
     */
    @GetMapping("/getRoleWithTool")
    public AjaxResult getRoleWithTool(long roleId) {

//        IndustryRoleEntity role = service.getById(roleId) ;
//        List<ToolEntity> tools =  roleToolService.findTools(roleId);
//
//        AjaxResult result = AjaxResult.success("操作成功" , role);
//        result.put("tools", tools);
//
//        return result ;

        return null ;
    }

    /**
     * 保存用户信息和工具信息 saveRoleWithTool
     */
    @PostMapping("/saveRoleWithTool")
    public AjaxResult saveRoleWithTool(@RequestBody @Validated RoleToolRequestDTO dto) {
        service.saveRoleWithTool(dto) ;
        return ok();
    }

    /**
     * 保存角色
     */
    @DataPermissionSave
    @PostMapping("/createRole")
    public AjaxResult createRole(@Validated @RequestBody IndustryRoleEntity e) {
        log.debug("save:{}", ToStringBuilder.reflectionToString(e));
        if(StringUtils.isBlank(e.getGreeting())){
            e.setGreeting(e.getResponsibilities());
        }
        service.createRole(e) ;
        return ok();
    }

    /**
     * 运行角色流程
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/listAllRole")
    public AjaxResult listAllRole(PermissionQuery query){

//        LambdaQueryWrapper<IndustryRoleEntity> wrapper = new LambdaQueryWrapper<>() ;
//        wrapper.setEntityClass(IndustryRoleEntity.class);
//        query.toWrapper(wrapper);
//        wrapper.orderByDesc(IndustryRoleEntity::getAddTime) ;

        List<IndustryRoleEntity> roleEntityList = service.listOrgRoleAndPublicRole(query) ;
        return AjaxResult.success(roleEntityList) ;
    }

    /**
     * 列出所有公共的角色
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/listPublicRole")
    public AjaxResult listPublicRole(PermissionQuery query){

//        LambdaQueryWrapper<IndustryRoleEntity> wrapper = new LambdaQueryWrapper<>() ;
//        wrapper.setEntityClass(IndustryRoleEntity.class);
//        query.toWrapper(wrapper);
//        wrapper.orderByDesc(IndustryRoleEntity::getAddTime) ;

        List<IndustryRoleEntity> roleEntityList = service.listPublicRole(query) ;
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

    @DataPermissionQuery
    @GetMapping("/catalogTreeSelect")
    public AjaxResult catalogTreeSelect(PermissionQuery query){
        IndustryRoleCatalogEntity e = new IndustryRoleCatalogEntity() ;
        e.setOrgId(CurrentAccountJwt.get().getOrgId());
        e.setOperatorId(CurrentAccountJwt.getUserId());
        e.setDepartmentId(CurrentAccountJwt.get().getDepartmentId());
        catalogService.getDefaultCatalog(e) ;
        return AjaxResult.success("success" , catalogService.selectCatalogTreeList(query)) ;
    }

    /**
     * 更新角色运行脚本
     * @return
     */
    @PostMapping("/updateRoleScript")
    public AjaxResult updateRoleScript(@RequestBody @Validated RoleScriptDto dto){
        log.debug("dto = {}", dto);
        service.updateRoleScript(dto);
        return ok() ;
    }

    /**
     * 推荐当前组织角色
     * @return
     */
    @GetMapping("/recommended")
    public AjaxResult recommended(long roleId){
        long orgId = CurrentAccountJwt.get().getOrgId();
        service.recommended(roleId , orgId);
        return ok() ;
    }

    /**
     * 验证脚本信息
     * @return
     */
    @PostMapping("/validateRoleScript")
    public AjaxResult validateRoleScript(@RequestBody @Validated RoleScriptDto dto){
        log.debug("dto = {}", dto);
        CompletableFuture<WorkflowExecutionDto> output = service.validateRoleScript(dto);
        return AjaxResult.success(output) ;
    }

    /**
     * 验证ReAct角色
     * @param dto
     * @return
     */
    @PostMapping("/validateReActRole")
    public AjaxResult validateReActRole(@RequestBody @Validated ReActRoleScriptDto dto){
        log.debug("dto = {}", dto);
        CompletableFuture<WorkflowExecutionDto> output = service.validateReActRole(dto);
        return AjaxResult.success(output) ;
    }

    /**
     * 市场角色列表
     * @return
     */
    @ResponseBody
    @PostMapping("/marketDatatables")
    public MarketTableDataInfo marketDatatables(Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));

        List<ConditionDto> condition =  page.getConditionList() ;

        ConditionDto dto = new ConditionDto() ;

        dto.setColumn("hasSale"); // 配置可销售角色
        dto.setValue("1");
        dto.setType("eq");

        condition.add(dto) ;
        page.setConditionList(condition);

        MarketTableDataInfo tableDataInfo = MarketTableDataInfo.form(this.toPage(model, this.getFeign(), page));

        // 查询出推送给组织的角色
        List<IndustryRoleEntity> pushOrgRoleList = rolePushOrgService.queryPushOrgRole(CurrentAccountJwt.get().getOrgId()) ;
        tableDataInfo.setPushRows(pushOrgRoleList);

        return tableDataInfo ;
    }

    /**
     * 录用员工
     * @return
     */
    @DataPermissionSave
    @GetMapping("/employRole")
    public AjaxResult employRole(@RequestParam String roleId , @RequestParam boolean isPush){

        Assert.notNull(roleId, "请选择录用角色");

        ManagerAccountDto dto = CurrentAccountJwt.get() ;
        service.employRole(Long.parseLong(roleId) ,
                dto.getOrgId() == null ? 0L : dto.getOrgId() ,
                dto.getId() == null ? 0L : dto.getId() ,
                dto.getDepartmentId() == null ? 0L : dto.getDepartmentId() ,
                isPush) ;

        return ok() ;
    }

    /**
     * 调整销售状态
     * @param field
     * @return
     */
    @ResponseBody
    @PostMapping("changeSaleField")
    public AjaxResult changeSaleField(@RequestBody FieldDto field) {
        log.debug("field = {}", field);

        // 如果字段值为9,则不允许销售 TODO 待提取出公共字段
        if (field.getValue().equals("9")) {
            return this.error("不允许设置，角色为不可销售状态");
        }

        LambdaUpdateWrapper<IndustryRoleEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(IndustryRoleEntity::getId, field.getId());
        lambdaUpdateWrapper.set(IndustryRoleEntity::getHasSale, field.getValue());

        boolean b = service.update(lambdaUpdateWrapper);
        return b ? this.ok() : this.error();
    }

    /**
     * 保存ReActRole配置
     * @return
     */
    @PostMapping("/saveRoleWithDeepSearchConfig")
    public AjaxResult saveRoleWithDeepSearchConfig(@RequestBody @Validated RoleDeepSearchConfigDto dto){

        log.debug("dto = {}", dto);
        service.saveRoleWithDeepSearchConfig(dto) ;

        return ok() ;
    }

    /**
     * 保存ReActRole配置
     * @return
     */
    @PostMapping("/saveRoleWithReActConfig")
    public AjaxResult saveRoleWithReActConfig(@RequestBody @Validated RoleReActConfigDto dto){

        log.debug("dto = {}", dto);
        service.saveRoleWithReActConfig(dto) ;

        return ok() ;
    }

    /**
     * 保存WorkflowRole配置
     * @return
     */
    @PostMapping("/saveRoleWithWorkflowConfig")
    public AjaxResult saveRoleWithWorkflowConfig(@RequestBody @Validated RoleFlowConfigDto dto){

        log.debug("dto = {}", dto);
        service.saveRoleWithWorkflowConfig(dto) ;

        return ok() ;
    }

    @DataPermissionSave
    @PostMapping("/modifyInfo")
    public AjaxResult modifyInfo(@RequestBody @Validated RoleInfoDto dto) throws Exception {
        // 修改基础信息
        service.modifyInfo(dto) ;

        return ok() ;
    }

    @Override
    public AjaxResult detail(@PathVariable String id) {
        IndustryRoleEntity e = service.findById(id) ;
        IndustryRoleDto dto = IndustryRoleDto.fromEntity(e) ;
        return AjaxResult.success(dto) ;
    }

    @Override
    public IIndustryRoleService getFeign() {
        return this.service;
    }
}