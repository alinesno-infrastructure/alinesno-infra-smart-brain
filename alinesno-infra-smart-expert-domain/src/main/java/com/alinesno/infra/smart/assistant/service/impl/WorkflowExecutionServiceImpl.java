package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.mapper.WorkflowExecutionMapper;
import com.alinesno.infra.smart.assistant.service.IWorkflowExecutionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class WorkflowExecutionServiceImpl extends IBaseServiceImpl<WorkflowExecutionEntity, WorkflowExecutionMapper> implements IWorkflowExecutionService {


    @Override
    public int getByChainName(String chainName) {

        LambdaQueryWrapper<WorkflowExecutionEntity> lq = new LambdaQueryWrapper<>() ;
        lq.eq(WorkflowExecutionEntity::getWorkflowName , chainName) ;

        return mapper.selectCount(lq).intValue() ;
    }
}
