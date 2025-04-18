package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.IDataAnalysisPlanService;
import com.alinesno.infra.smart.scene.entity.DataAnalysisPlanEntity;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据分析规划控制器
 */
@Slf4j
@Api(tags = "数据分析规划")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/dataAnalysisPlan/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class DataAnalysisPlanController extends BaseController<DataAnalysisPlanEntity, IDataAnalysisPlanService> {

    @Autowired
    private IDataAnalysisPlanService service;

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

    @Override
    public IDataAnalysisPlanService getFeign() {
        return this.service;
    }

}    