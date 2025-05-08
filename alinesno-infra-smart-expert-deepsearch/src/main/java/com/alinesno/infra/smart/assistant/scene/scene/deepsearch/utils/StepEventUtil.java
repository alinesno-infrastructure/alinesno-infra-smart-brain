package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;

import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息通知事件
 */
@Data
@Component
public class StepEventUtil {

    @Autowired
    private StreamMessagePublisher streamMessagePublisher;

    private IndustryRoleEntity role;
    private MessageTaskInfo taskInfo;

    public void eventStepMessage(DeepSearchFlow deepSearchFlow , IndustryRoleEntity role , MessageTaskInfo taskInfo) {
        taskInfo.setDeepSearchFlow(deepSearchFlow);
        streamMessagePublisher.doStuffAndPublishAnEvent(null,
                role,
                taskInfo,
                taskInfo.getTraceBusId()
        );
    }

}
