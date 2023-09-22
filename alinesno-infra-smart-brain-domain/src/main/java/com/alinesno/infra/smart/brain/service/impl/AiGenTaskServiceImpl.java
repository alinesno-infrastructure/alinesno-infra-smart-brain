package com.alinesno.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.entity.AiGenTaskEntity;
import com.alinesno.infra.smart.brain.mapper.AiGenTaskMapper;
import com.alinesno.infra.smart.brain.service.IAiGenTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiGenTaskServiceImpl extends IBaseServiceImpl<AiGenTaskEntity , AiGenTaskMapper> implements IAiGenTaskService {

    @Override
    public void commitTask(BrainTaskDto dto) {

        AiGenTaskEntity entity = new AiGenTaskEntity() ;
        BeanUtils.copyProperties(dto , entity);

        this.save(entity) ;
    }

    @Override
    public void commitTaskToCms(BrainTaskDto dto) {

        AiGenTaskEntity entity = new AiGenTaskEntity() ;
        BeanUtils.copyProperties(dto , entity);

        this.save(entity) ;
    }
}
