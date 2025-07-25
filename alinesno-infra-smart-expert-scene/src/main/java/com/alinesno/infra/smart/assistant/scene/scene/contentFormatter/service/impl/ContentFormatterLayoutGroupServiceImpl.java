package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.GroupTypeEnums;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterLayoutGroupMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterLayoutMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterRuleMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterLayoutGroupService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutGroupEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterRuleEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ContentFormatterLayoutGroupServiceImpl extends IBaseServiceImpl<ContentFormatterLayoutGroupEntity, ContentFormatterLayoutGroupMapper> implements IContentFormatterLayoutGroupService {

    @Autowired
    private ContentFormatterRuleMapper ruleMapper;

    @Autowired
    private ContentFormatterLayoutMapper layoutMapper ;

    @Override
    public void removeGroup(Long id, String groupType) {
        removeById(id) ;

        if(GroupTypeEnums.LAYOUT.getValue().equals(groupType)){
            layoutMapper.delete(new LambdaQueryWrapper<ContentFormatterLayoutEntity>()
                    .eq(ContentFormatterLayoutEntity::getGroupId, id));
        }else if(GroupTypeEnums.AUDIT.getValue().equals(groupType)){
            // 删除分组下面的规则
            ruleMapper.delete(new LambdaQueryWrapper<ContentFormatterRuleEntity>()
                    .eq(ContentFormatterRuleEntity::getGroupId, id));
        }

    }

    @Override
    public List<ContentFormatterLayoutGroupEntity> listOrgGroup(PermissionQuery query) {

        LambdaQueryWrapper<ContentFormatterLayoutGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(ContentFormatterLayoutGroupEntity.class) ;
        queryWrapper.eq(ContentFormatterLayoutGroupEntity::getDataScope, SceneScopeType.ORG_SCENE.getValue())
                .eq(ContentFormatterLayoutGroupEntity::getGroupType, GroupTypeEnums.LAYOUT.getValue())
                .eq(ContentFormatterLayoutGroupEntity::getOrgId, query.getOrgId());

        return list(queryWrapper) ;
    }
}