package com.alinesno.infra.smart.assistant.workflow.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowExecutionEntity;

/**
 * 工作流执行服务接口，主要负责工作流当前执行实例情况的业务逻辑。
 * 继承自 IBaseService 接口，通过其提供的通用方法，能够对工作流执行实例数据进行基本操作。
 * 
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IFlowExecutionService extends IBaseService<FlowExecutionEntity> {
}