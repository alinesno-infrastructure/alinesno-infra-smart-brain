package com.alinesno.infra.smart.assistant.scene.scene.documentReader.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.dto.DocReaderInitDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.service.IDocReaderSceneService;
import com.alinesno.infra.smart.scene.entity.DocReaderSceneEntity;
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
@RequestMapping("/api/infra/smart/assistant/scene/documentReader/sceneInfo")
public class DocReaderSceneInfoController extends BaseController<DocReaderSceneEntity, IDocReaderSceneService> {

    @Autowired
    private IDocReaderSceneService service;

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
     * 初始化智能助手
     * @return
     */
    @PostMapping("/initAgents")
    public AjaxResult initAgents(@RequestBody DocReaderInitDto dto){

        service.initAgents(dto) ;

        return AjaxResult.success();
    }

    @Override
    public IDocReaderSceneService getFeign() {
        return this.service;
    }
}