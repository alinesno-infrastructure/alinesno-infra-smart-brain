package com.alinesno.infra.smart.assistant.workplace.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceAddDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceEntity;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceSourceEnums;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceType;
import com.alinesno.infra.smart.assistant.workplace.mapper.WorkplaceMapper;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

    @Override
    public List<WorkplaceEntity> listOrgWorkplace(Long orgId) {

        LambdaQueryWrapper<WorkplaceEntity> queryWrapper = Wrappers.lambdaQuery(WorkplaceEntity.class)
                .and(wrapper -> wrapper
                        .eq(WorkplaceEntity::getWorkplaceSource, WorkplaceSourceEnums.BACKEND.getCode())
                        .eq(WorkplaceEntity::getOrgId, orgId)
                        .eq(WorkplaceEntity::getWorkplaceType, WorkplaceType.ORG.getId()))
                .or()
                .eq(WorkplaceEntity::getWorkplaceType, WorkplaceType.PUBLIC.getId());

        // 执行查询
        List<WorkplaceEntity> workplaceEntities = list(queryWrapper);

        return workplaceEntities == null ? Collections.emptyList() : workplaceEntities ;
    }

}
