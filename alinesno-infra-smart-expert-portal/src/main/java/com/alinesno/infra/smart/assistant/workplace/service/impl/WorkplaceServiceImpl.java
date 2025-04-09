package com.alinesno.infra.smart.assistant.workplace.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceAddDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceEntity;
import com.alinesno.infra.smart.assistant.workplace.mapper.WorkplaceMapper;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkplaceServiceImpl extends IBaseServiceImpl<WorkplaceEntity, WorkplaceMapper> implements IWorkplaceService {

    @Override
    public Long createWorkplace(WorkplaceAddDto dto) {

        WorkplaceEntity entity = new WorkplaceEntity();
        BeanUtils.copyProperties(dto , entity) ;

        save(entity) ;

        return entity.getId() ;
    }
}
