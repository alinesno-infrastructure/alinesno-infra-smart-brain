package com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewAuditDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewAuditService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewRulesService;
import com.alinesno.infra.smart.scene.entity.DocReviewAuditEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import com.alinesno.infra.smart.scene.enums.ReviewPositionEnums;
import com.alinesno.infra.smart.scene.enums.RiskLevelEnums;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/infra/smart/assistant/scene/documentReview/audit")
public class DocReviewAuditController extends BaseController<DocReviewAuditEntity, IDocReviewAuditService> {

    @Autowired
    private IDocReviewAuditService service;

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

    /**
     * 新增加审核清单
     * @return
     */
    @DataPermissionSave
    @PostMapping("/saveOrUpdateAudit")
    public AjaxResult addAudit(@RequestBody @Validated DocReviewAuditDto dto) {

        Assert.notNull(SceneScopeType.getByValue(dto.getDataScope()) , "数据范围错误");
        log.debug("新增审核清单：{}", ToStringBuilder.reflectionToString(dto));
        Long id = service.saveOrUpdateAudit(dto);
        return AjaxResult.success("操作成功" , id) ;
    }

    /**
     * 查询审核清单
     * @param id
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/queryAudit")
    public AjaxResult queryAudit(@RequestParam Long id) {
        DocReviewAuditDto auditDto = service.queryAudit(id);
        return AjaxResult.success(auditDto)  ;
    }

    @Override
    public IDocReviewAuditService getFeign() {
        return this.service;
    }
}