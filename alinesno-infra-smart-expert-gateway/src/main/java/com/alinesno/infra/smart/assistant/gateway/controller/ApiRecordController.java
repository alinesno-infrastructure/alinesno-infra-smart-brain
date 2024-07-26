package com.alinesno.infra.smart.assistant.gateway.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.excel.utils.ExcelUtil;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.common.web.log.annotation.Log;
import com.alinesno.infra.common.web.log.bo.SysOperLogBo;
import com.alinesno.infra.common.web.log.enums.BusinessType;
import com.alinesno.infra.smart.assistant.entity.ApiRecordEntity;
import com.alinesno.infra.smart.assistant.service.IApiRecordService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理与ApiRecordEntity相关的请求的Controller。
 * 继承自BaseController类并实现IApiRecordService接口。
 *
 * @author LuoXiaoDong
 * @version 1.0.0
 */
@Api(tags = "ApiRecord")
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/base/sensitive/api_record")
public class ApiRecordController extends BaseController<ApiRecordEntity, IApiRecordService> {

    // 日志记录
    private static final Logger log = LoggerFactory.getLogger(ApiRecordController.class);

    @Autowired
    private IApiRecordService service;

    /**
     * 获取ApiRecordEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model   Model对象。
     * @param page    DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    @Log(title = "查询搜索" , businessType = BusinessType.SEARCH)
    @Override
    public AjaxResult count() {
        return super.count();
    }

    /**
     * 导出操作日志记录列表
     */
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysOperLogBo operLog, HttpServletResponse response) {
        List<ApiRecordEntity> list = service.selectOperLogList(operLog);
        ExcelUtil.exportExcel(list, "操作日志", ApiRecordEntity.class, response);
    }

    /**
     * 批量删除操作日志记录
     *
     * @param operIds 日志ids
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds) {
        return toAjax(service.deleteOperLogByIds(operIds));
    }

    /**
     * 清理操作日志记录
     */
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        service.cleanOperLog();
        return AjaxResult.success();
    }

    @Override
    public IApiRecordService getFeign() {
        return this.service;
    }
}
