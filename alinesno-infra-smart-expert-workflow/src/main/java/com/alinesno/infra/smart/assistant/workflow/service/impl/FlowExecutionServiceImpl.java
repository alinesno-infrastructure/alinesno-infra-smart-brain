package com.alinesno.infra.smart.assistant.workflow.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowExecutionEntity;
import com.alinesno.infra.smart.assistant.workflow.mapper.FlowExecutionMapper;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 工作流执行服务接口的实现类，专注于工作流当前执行实例情况的具体业务逻辑处理。
 * 继承自 IBaseServiceImpl 类，通过其提供的通用方法完成对工作流执行实例数据的基本操作。
 * 
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class FlowExecutionServiceImpl extends IBaseServiceImpl<FlowExecutionEntity, FlowExecutionMapper> implements IFlowExecutionService {
    // 后续可根据具体业务需求添加额外的业务逻辑实现
}