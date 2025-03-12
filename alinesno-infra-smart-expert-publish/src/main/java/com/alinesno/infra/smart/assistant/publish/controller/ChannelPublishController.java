package com.alinesno.infra.smart.assistant.publish.controller;

import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;
import com.alinesno.infra.smart.assistant.publish.enums.ChannelListEnums;
import com.alinesno.infra.smart.assistant.publish.service.IChannelPublishService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * ChannelPublishController类用于处理与渠道发布相关的API请求。
 * 它继承自BaseController，用于提供基础的CRUD操作。
 * 该控制器使用原型作用域，确保每次请求都会创建一个新的实例。
 */
@Slf4j
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/channelPublish")
public class ChannelPublishController extends BaseController<ChannelPublishEntity, IChannelPublishService> {

    @Autowired
    private IChannelPublishService service;

    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    @GetMapping("/channels")
    public AjaxResult getChannelList() {
        return AjaxResult.success(ChannelListEnums.toListOfMaps());
    }

    @Override
    public IChannelPublishService getFeign() {
        return this.service;
    }
}
