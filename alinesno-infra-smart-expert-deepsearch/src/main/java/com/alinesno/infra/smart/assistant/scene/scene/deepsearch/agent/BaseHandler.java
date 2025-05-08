package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.StepEventUtil;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public abstract class BaseHandler {

    private StreamMessagePublisher streamMessagePublisher;

    private IndustryRoleEntity role;

    private DeepSearchFlow deepSearchFlow;

    private StepEventUtil stepEventUtil;

    private MessageTaskInfo taskInfo;

    private IToolService toolService;

    private Map<String , String> secretKey ; // 组织配置密钥

    private List<ToolDto> tools ;  // 工具列表
}
