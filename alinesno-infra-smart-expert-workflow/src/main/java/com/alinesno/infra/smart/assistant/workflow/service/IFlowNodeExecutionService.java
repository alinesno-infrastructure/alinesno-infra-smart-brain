package com.alinesno.infra.smart.assistant.workflow.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeExecutionEntity;

/**
 * 工作流节点执行服务接口，用于处理工作流节点执行情况的业务。
 * 继承自 IBaseService 接口，可使用其通用方法对工作流节点执行数据进行常规操作。
 * 
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IFlowNodeExecutionService extends IBaseService<FlowNodeExecutionEntity> {
}