package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.im.entity.MessageReferenceEntity;
import com.alinesno.infra.smart.im.service.IMessageReferenceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消息引用控制
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/base/im/messageReference")
public class MessageReferenceController {

    @Autowired
    private IMessageReferenceService messageReferenceService;

    /**
     * 通过消息获取到引用
     */
    @RequestMapping("/getByMessageId")
    public AjaxResult getByMessageId(@RequestParam String messageId) {

        LambdaQueryWrapper<MessageReferenceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageReferenceEntity::getMessageId, messageId);

        List<MessageReferenceEntity> messageReferences = messageReferenceService.list(queryWrapper);

        return AjaxResult.success("查询成功." , messageReferences);
    }
}
