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
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterLayoutGroupDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterLayoutGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterParseService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutGroupEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterParseEntity;
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
 * 数据分析规划控制器
 */
@Slf4j
@Api(tags = "数据分析规划")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterLayoutGroup/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterLayoutGroupController extends BaseController<ContentFormatterLayoutGroupEntity, IContentFormatterLayoutGroupService> {

    @Autowired
    private IContentFormatterLayoutGroupService service;

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

    @ApiOperation("获取分组列表")
    @GetMapping("listGroups")
    public AjaxResult listGroups() {
        List<ContentFormatterLayoutGroupEntity> rules = service.findAll();
        return AjaxResult.success(rules);
    }

    @ApiOperation("获取分组详情")
    @DataPermissionQuery
    @GetMapping("getGroup")
    public AjaxResult getGroup(@RequestParam Long id , PermissionQuery query) {
        ContentFormatterLayoutGroupEntity rule = service.findById(id);
        if (rule == null) {
            return AjaxResult.error("分组不存在" , ResultCodeEnum.NOT_FOUND);
        }
        return AjaxResult.success(rule);
    }

    @ApiOperation("添加分组")
    @DataPermissionSave
    @PostMapping("addGroup")
    public AjaxResult addGroup(@Validated @RequestBody ContentFormatterLayoutGroupDto dto) {
        ContentFormatterLayoutGroupEntity entity = new ContentFormatterLayoutGroupEntity();
        BeanUtils.copyProperties(dto, entity);
        service.save(entity);
        return AjaxResult.success("添加成功");
    }

    @ApiOperation("更新分组")
    @DataPermissionSave
    @PutMapping("updateGroup")
    public AjaxResult updateGroup(@Validated @RequestBody ContentFormatterLayoutGroupDto dto) {
        if (dto.getId() == null) {
            return AjaxResult.error("ID不能为空" , ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }

        ContentFormatterLayoutGroupEntity entity = service.findById(dto.getId());
        if (entity == null) {
            return AjaxResult.error("分组不存在" , ResultCodeEnum.NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, entity);
        service.update(entity);
        return AjaxResult.success("更新成功");
    }

    @ApiOperation("删除分组")
    @DeleteMapping("delGroup")
    public AjaxResult delGroup(@RequestParam Long id) {
        service.removeById(id);
        return AjaxResult.success("删除成功");
    }

    /**
     * 通过分组类型获取到分组列表
     * @return
     */

    @Override
    public IContentFormatterLayoutGroupService getFeign() {
        return this.service;
    }

}