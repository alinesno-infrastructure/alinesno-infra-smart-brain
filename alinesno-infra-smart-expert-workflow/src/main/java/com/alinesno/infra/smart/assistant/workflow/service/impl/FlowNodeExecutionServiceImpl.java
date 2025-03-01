package com.alinesno.infra.smart.assistant.workflow.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeExecutionEntity;
import com.alinesno.infra.smart.assistant.workflow.mapper.FlowNodeExecutionMapper;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowNodeExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 工作流节点执行服务接口的实现类，负责处理工作流节点执行情况的具体业务逻辑。
 * 继承自 IBaseServiceImpl 类，使用其通用方法对工作流节点执行数据进行常规操作。
 * 
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class FlowNodeExecutionServiceImpl extends IBaseServiceImpl<FlowNodeExecutionEntity, FlowNodeExecutionMapper> implements IFlowNodeExecutionService {
    // 后续可根据具体业务需求添加额外的业务逻辑实现
}