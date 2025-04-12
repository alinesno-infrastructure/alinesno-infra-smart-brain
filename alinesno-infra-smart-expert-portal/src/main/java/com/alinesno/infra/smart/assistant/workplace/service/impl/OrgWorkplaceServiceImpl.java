package com.alinesno.infra.smart.assistant.workplace.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.workplace.dto.OrgWorkplaceDto;
import com.alinesno.infra.smart.assistant.workplace.entity.OrgWorkplaceEntity;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceEntity;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceItemEntity;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceItemTypeEnums;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceSourceEnums;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceType;
import com.alinesno.infra.smart.assistant.workplace.mapper.OrgWorkplaceMapper;
import com.alinesno.infra.smart.assistant.workplace.service.IOrgWorkplaceService;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceItemService;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceService;
import com.alinesno.infra.smart.im.service.IChannelService;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrgWorkplaceServiceImpl extends IBaseServiceImpl<OrgWorkplaceEntity, OrgWorkplaceMapper> implements IOrgWorkplaceService {

    @Autowired
    private IWorkplaceService workplaceService ;

    @Autowired
    private IWorkplaceItemService workplaceItemService ;

    @Autowired
    private ISceneService sceneService ;

    @Autowired
    private IChannelService channelService ;

    /**
     * 组织使用指定的工作台，处理过程
     * 1. 通过workplaceId复制工作台和工作台关联的Item到当前组织下面，然后修改名称
     * 2. 在OrgWorkplace表中添加一条记录，表示当前组织已经有使用的工作台
     */
    @Override
    public void useWorkplace(OrgWorkplaceDto dto) {

        WorkplaceEntity workplace = workplaceService.getById(dto.getWorkplaceId())  ;

        // 保存新的用户工作台
        WorkplaceEntity newWorkplace = new WorkplaceEntity() ;
        BeanUtils.copyProperties(workplace, newWorkplace);

        newWorkplace.setId(null);
        newWorkplace.setName(dto.getWorkplaceName());
        newWorkplace.setOrgId(dto.getOrgId());
        newWorkplace.setOperatorId(dto.getOperatorId());
        newWorkplace.setWorkplaceType(WorkplaceType.ORG.getId());

        newWorkplace.setSourceWorkplaceId(workplace.getId());
        newWorkplace.setWorkplaceSource(WorkplaceSourceEnums.FRONTEND.getCode());

        workplaceService.save(newWorkplace) ;

        // 复制Agent角色ID信息
        LambdaQueryWrapper<WorkplaceItemEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WorkplaceItemEntity::getWorkplaceId, workplace.getId());

        List<WorkplaceItemEntity> list = workplaceItemService.list(lambdaQueryWrapper);

        List<Long> oldSceneIds = list.stream()
                .filter(item -> item.getItemType().equals(WorkplaceItemTypeEnums.SCENE.getCode()))
                .map(WorkplaceItemEntity::getItemId)
                .toList();

        if(!list.isEmpty()){
           for(WorkplaceItemEntity item : list) {
               item.setId(null);
               item.setWorkplaceId(newWorkplace.getId());
           }
        }
        workplaceItemService.saveBatch(list) ;

        List<Long> newSceneIds = list.stream()
                .filter(item -> item.getItemType().equals(WorkplaceItemTypeEnums.SCENE.getCode()))
                .map(WorkplaceItemEntity::getItemId)
                .toList();

        // 然后保存到OrgWorkplace表中
        OrgWorkplaceEntity orgWorkplaceEntity = new OrgWorkplaceEntity() ;
        orgWorkplaceEntity.setWorkplaceId(newWorkplace.getId());
        orgWorkplaceEntity.setOrgId(dto.getOrgId());
        save(orgWorkplaceEntity) ;

    }

    @Override
    public Long isHasWorkplace(Long orgId) {

        LambdaQueryWrapper<OrgWorkplaceEntity> query = Wrappers.lambdaQuery(OrgWorkplaceEntity.class) ;
        query.eq(OrgWorkplaceEntity::getOrgId, orgId) ;

        OrgWorkplaceEntity orgWorkplaceEntity = getOne(query);

        return orgWorkplaceEntity == null ? null : orgWorkplaceEntity.getWorkplaceId() ;
    }

    @Override
    public WorkplaceEntity getCurrentWorkplace(Long orgId) {

        LambdaQueryWrapper<OrgWorkplaceEntity> query = Wrappers.lambdaQuery(OrgWorkplaceEntity.class) ;
        query.eq(OrgWorkplaceEntity::getOrgId, orgId) ;

        OrgWorkplaceEntity orgWorkplaceEntity = getOne(query);
        if (orgWorkplaceEntity != null) {
            return workplaceService.getById(orgWorkplaceEntity.getWorkplaceId()) ;
        }

        return null ;
    }
}
