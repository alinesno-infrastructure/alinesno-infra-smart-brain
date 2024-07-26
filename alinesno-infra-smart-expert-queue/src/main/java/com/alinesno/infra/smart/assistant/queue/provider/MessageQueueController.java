package com.alinesno.infra.smart.assistant.queue.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
import com.alinesno.infra.smart.assistant.entity.MessageQueueEntity;
import com.alinesno.infra.smart.assistant.service.IMessageQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供消息服务列表
 */
@Slf4j
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/message")
public class MessageQueueController extends SuperController {

    @Autowired
    private IMessageQueueService queueService ;

    /**
     * 添加消息服务
     * @param entity
     * @return
     */
    @PostMapping("/addMessage")
    public AjaxResult addMessage(@RequestBody MessageQueueEntity entity){
        queueService.addMessage(entity);
        return AjaxResult.success();
    }

    /**
     * 查询消息服务
     * @param businessId
     * @return
     */
    @PostMapping("/queryMessage")
    public MessageQueueEntity queryMessage(String businessId){
        return queueService.queryMessage(businessId);
    }

    /**
     * 更新内容
     * @param dto
     * @return
     */
    @PostMapping("/modifyContent")
    public boolean modifyContent(@RequestBody TaskContentDto dto){
        return queueService.updateAssistantContent(dto.getBusinessId() , dto.getGenContent());
    }
}
