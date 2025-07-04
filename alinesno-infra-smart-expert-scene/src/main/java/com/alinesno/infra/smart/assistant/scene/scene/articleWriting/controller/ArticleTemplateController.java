package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.enums.ArticleCategoryEnums;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleTemplateService;
import com.alinesno.infra.smart.scene.entity.ArticleTemplateEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/infra/smart/assistant/scene/articleTemplate")
public class ArticleTemplateController extends BaseController<ArticleTemplateEntity, IArticleTemplateService> {

    @Autowired
    private IArticleTemplateService service;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model   Model对象。
     * @param page    DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 获取所有文章分类
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getAllArticleTemplate")
    public AjaxResult getAllArticleTemplate(PermissionQuery query) {
        return AjaxResult.success(ArticleCategoryEnums.getAllCategories());
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/getTemplateDetail")
    public AjaxResult getTemplateDetail(@RequestParam(value = "templateId") Long templateId) {
        return AjaxResult.success(service.getById(templateId));
    }

    /**
     * 获取所有的模板
     *
     * @param query
     * @return
     * @throws Exception
     */
    @DataPermissionQuery
    @GetMapping("/getTemplateByType")
    public AjaxResult getTemplateByType(PermissionQuery query , String typeCode) throws Exception {
        return AjaxResult.success(service.getTemplateByType(query , typeCode));
    }

    @DataPermissionSave
    @ResponseBody
    @PostMapping("/saveArticleTemplate")
    public AjaxResult saveArticleTemplate(@RequestBody ArticleTemplateEntity entity) throws Exception {
        service.save(entity);
        return this.ok();
    }

    @Override
    public IArticleTemplateService getFeign() {
        return this.service;
    }
}