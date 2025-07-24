package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.ResultCodeEnum;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterRuleDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterRuleService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterRuleEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审核规则管理
 */
@Slf4j
@Api(tags = "内容格式化规则管理")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterRule/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterRuleController extends BaseController<ContentFormatterRuleEntity, IContentFormatterRuleService> {

    @Autowired
    private IContentFormatterRuleService service;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    @ApiOperation("获取规则列表")
    @GetMapping("listRules")
    public AjaxResult listRules() {
        List<ContentFormatterRuleEntity> rules = service.findAll();
        return AjaxResult.success(rules);
    }

    @ApiOperation("获取规则详情")
    @DataPermissionQuery
    @GetMapping("getRule")
    public AjaxResult getRule(@RequestParam Long id , PermissionQuery query) {
        ContentFormatterRuleEntity rule = service.findById(id);
        if (rule == null) {
            return AjaxResult.error("规则不存在" , ResultCodeEnum.NOT_FOUND);
        }
        return AjaxResult.success(rule);
    }

    @ApiOperation("添加规则")
    @DataPermissionSave
    @PostMapping("addRule")
    public AjaxResult addRule(@Validated @RequestBody ContentFormatterRuleDto dto) {
        ContentFormatterRuleEntity entity = new ContentFormatterRuleEntity();
        BeanUtils.copyProperties(dto, entity);
        service.save(entity);
        return AjaxResult.success("添加成功");
    }

    @ApiOperation("更新规则")
    @DataPermissionSave
    @PutMapping("updateRule")
    public AjaxResult updateRule(@Validated @RequestBody ContentFormatterRuleDto dto) {
        if (dto.getId() == null) {
            return AjaxResult.error("ID不能为空" , ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }

        ContentFormatterRuleEntity entity = service.findById(dto.getId());
        if (entity == null) {
            return AjaxResult.error("规则不存在" , ResultCodeEnum.NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, entity);
        service.update(entity);
        return AjaxResult.success("更新成功");
    }

    @ApiOperation("删除规则")
    @DeleteMapping("delRule")
    public AjaxResult delRule(@RequestParam Long id) {
        service.removeById(id);
        return AjaxResult.success("删除成功");
    }

    @Override
    public IContentFormatterRuleService getFeign() {
        return this.service;
    }
}