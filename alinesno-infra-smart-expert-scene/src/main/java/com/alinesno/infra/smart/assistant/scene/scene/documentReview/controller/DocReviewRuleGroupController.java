package com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRuleGroupDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRuleGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRulesService;
import com.alinesno.infra.smart.scene.entity.DocReviewRuleGroupEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import com.alinesno.infra.smart.scene.enums.RiskLevelEnums;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/assistant/scene/documentReview/ruleGroup")
public class DocReviewRuleGroupController extends BaseController<DocReviewRuleGroupEntity, IDocReviewRuleGroupService> {

    @Autowired
    private IDocReviewRuleGroupService service;

    /**
     * 添加分组
     * @return
     */
    @DataPermissionSave
    @PostMapping("/saveOrUpdateRuleGroup")
    public AjaxResult saveOrUpdateRuleGroup(@RequestBody @Validated DocReviewRuleGroupDto dto) {

        DocReviewRuleGroupEntity entity = new DocReviewRuleGroupEntity();
        BeanUtil.copyProperties(dto , entity);

        service.saveOrUpdate(entity);
        return ok();
    }

    /**
     * 查询所有分组
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/listGroup")
    public AjaxResult listGroup(PermissionQuery query) {
        List<DocReviewRuleGroupDto> list = service.listGroup(query) ;
        return AjaxResult.success("操作成功", list)  ;
    }

    /**
     * 删除分组
     * @return
     */
    @DeleteMapping("/deleteGroup")
    public AjaxResult deleteGroup(@RequestParam Long id) {
        service.removeGroup(id);
        return ok();
    }

    @Override
    public IDocReviewRuleGroupService getFeign() {
        return this.service;
    }
}