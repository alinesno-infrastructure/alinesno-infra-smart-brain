package com.alinesno.infra.smart.assistant.workflow.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeEntity;

/**
 * 工作流节点服务接口，专注于工作流节点情况信息的业务处理。
 * 继承自 IBaseService 接口，利用其通用功能来实现对工作流节点数据的常规操作。
 * 
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IFlowNodeService extends IBaseService<FlowNodeEntity> {
    // 可后续根据具体业务需求添加自定义方法
}