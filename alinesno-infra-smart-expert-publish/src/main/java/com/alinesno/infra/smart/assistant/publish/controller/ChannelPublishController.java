package com.alinesno.infra.smart.assistant.publish.controller;

import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.publish.dto.ChannelPublishDTO;
import com.alinesno.infra.smart.assistant.publish.entity.ChannelPublishEntity;
import com.alinesno.infra.smart.assistant.publish.enums.ChannelListEnums;
import com.alinesno.infra.smart.assistant.publish.service.IChannelPublishService;
import com.alinesno.infra.smart.assistant.publish.utils.ApiKeyGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 更新渠道配置
     * @param dto
     * @return
     */
    @DataPermissionSave
    @PostMapping("/updateChannelConfig")
    public AjaxResult updateChannelConfig(@RequestBody @Validated ChannelPublishDTO dto) {
        log.debug("dto = {}", ToStringBuilder.reflectionToString(dto));

        if(StringUtils.isBlank(dto.getApiKey())){
            dto.setApiKey(ApiKeyGenerator.generateApiKey()) ;
        }

        dto.setHasConfigured(1);

        ChannelPublishEntity entity = dto.toEntity();
        log.debug("entity = {}", entity);

        service.updateConfig(entity);

        return AjaxResult.success();
    }

    @DataPermissionQuery
    @GetMapping("/channels")
    public AjaxResult getChannelList(PermissionQuery query , @RequestParam long roleId) {
        List<Map<String, Object>> channelList = ChannelListEnums.toListOfMaps();

        // 获取所有渠道的 paramKey
        List<String> paramKeys = channelList.stream()
                .map(map -> map.get("paramKey").toString())
                .toList();

        // 查询配置信息
        Map<String, ChannelPublishEntity> configMap = service.getConfigsByParamKeys(paramKeys , query , roleId);

        // 将配置信息添加到结果中
        for (Map<String, Object> channel : channelList) {
            String paramKey = channel.get("paramKey").toString();
            ChannelPublishEntity config = configMap.get(paramKey);
            if (config != null) {
                Map<String, Object> configDtoMap = ChannelPublishDTO.toMap(config);
                channel.putAll(configDtoMap) ;
            }
        }

        return AjaxResult.success(channelList);
    }


    @Override
    public IChannelPublishService getFeign() {
        return this.service;
    }
}
