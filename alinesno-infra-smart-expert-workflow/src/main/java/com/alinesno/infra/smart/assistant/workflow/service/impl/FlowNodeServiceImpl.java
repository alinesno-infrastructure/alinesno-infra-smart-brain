package com.alinesno.infra.smart.assistant.workflow.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeEntity;
import com.alinesno.infra.smart.assistant.workflow.mapper.FlowNodeMapper;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 工作流节点服务接口的实现类，主要处理工作流节点情况信息的具体业务逻辑。
 * 继承自 IBaseServiceImpl 类，利用其通用功能实现对工作流节点数据的常规操作。
 * 
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class FlowNodeServiceImpl extends IBaseServiceImpl<FlowNodeEntity, FlowNodeMapper> implements IFlowNodeService {
    // 后续可根据具体业务需求添加额外的业务逻辑实现
}