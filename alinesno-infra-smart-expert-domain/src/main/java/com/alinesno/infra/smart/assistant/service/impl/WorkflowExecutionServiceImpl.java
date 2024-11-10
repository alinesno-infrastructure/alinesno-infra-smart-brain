package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.mapper.WorkflowExecutionMapper;
import com.alinesno.infra.smart.assistant.service.IWorkflowExecutionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void saveRecord(WorkflowExecutionDto record) {

        WorkflowExecutionEntity entity = new WorkflowExecutionEntity() ;
        BeanUtils.copyProperties(record, entity);

        entity.setId(IdUtil.getSnowflakeNextId());

        this.save(entity);
    }

}


