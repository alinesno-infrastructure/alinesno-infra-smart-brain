package com.alinesno.infra.smart.brain.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.brain.entity.GenerateTaskHistoryEntity;
import com.alinesno.infra.smart.brain.service.IGenerateTaskHistoryService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
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
@Api(tags = "BusinessLog")
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/smart/brain/generateTaskHistory")
public class GenerateTaskHistoryController extends BaseController<GenerateTaskHistoryEntity, IGenerateTaskHistoryService> {

    @Autowired
    private IGenerateTaskHistoryService service;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     *
     * @return
     */
    @SneakyThrows
    @GetMapping("/getPromptContent")
    public AjaxResult getPromptContent(String taskId){

        GenerateTaskHistoryEntity e = service.getById(taskId)  ;
        JSONObject prompts = JSONObject.parseObject(e.getParams() , JSONObject.class) ;

        return AjaxResult.success(prompts) ;
    }

    @Override
    public IGenerateTaskHistoryService getFeign() {
        return this.service;
    }
}

