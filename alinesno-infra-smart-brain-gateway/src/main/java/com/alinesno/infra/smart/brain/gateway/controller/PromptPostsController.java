package com.alinesno.infra.smart.brain.gateway.controller;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.brain.entity.PromptPostsEntity;
import com.alinesno.infra.smart.brain.service.IPromptPostsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
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
@RequestMapping("/api/infra/smart/brain/promptPosts")
public class PromptPostsController extends BaseController<PromptPostsEntity, IPromptPostsService> {

    @Autowired
    private IPromptPostsService service;

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

    @Override
    public AjaxResult save(Model model, @RequestBody PromptPostsEntity entity) throws Exception {
        service.savePromptPost(entity) ;
        return this.ok();
    }

    /**
     *
     * @return
     */
    @GetMapping("/getPromptContent")
    public AjaxResult getPromptContent(String postId){
        PromptPostsEntity e = service.getById(postId)  ;

        List<PromptMessageDto> prompts = JSONArray.parseArray(e.getPromptContent() , PromptMessageDto.class) ;

        return AjaxResult.success(prompts) ;
    }

    /**
     *
     * @return
     */
    @PostMapping("/updatePromptContent")
    public AjaxResult updatePromptContent(@RequestBody List<PromptMessageDto> messageDto , String postId){

        service.updatePromptContent(messageDto , postId) ;

        return ok() ;
    }

    @Override
    public IPromptPostsService getFeign() {
        return this.service;
    }
}

