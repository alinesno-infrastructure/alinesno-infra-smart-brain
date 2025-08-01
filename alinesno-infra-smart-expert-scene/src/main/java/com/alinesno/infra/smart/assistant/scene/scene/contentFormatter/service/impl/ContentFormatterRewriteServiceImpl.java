package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterRewriteConfigDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterRewriteMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterConfigService;
import com.alinesno.infra.smart.scene.entity.ContentFormatterConfigEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContentFormatterRewriteServiceImpl extends IBaseServiceImpl<ContentFormatterConfigEntity, ContentFormatterRewriteMapper> implements IContentFormatterConfigService {

    @Override
    public void updateRewriteConfig(ContentFormatterRewriteConfigDto dto) {

        LambdaQueryWrapper<ContentFormatterConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContentFormatterConfigEntity::getSceneId, dto.getSceneId())
                .eq(ContentFormatterConfigEntity::getOrgId , dto.getOrgId()) ;

        ContentFormatterConfigEntity entity = getOne(queryWrapper);

        if(entity == null) {
            entity = new ContentFormatterConfigEntity() ;

            entity.setSceneId(dto.getSceneId());
            entity.setOrgId(dto.getOrgId());
            entity.setOperatorId(dto.getOperatorId());
            entity.setDepartmentId(dto.getDepartmentId());

            entity.setRewriteConfig(JSONObject.toJSONString(dto.getRewriteConfig()));
            entity.setDataScope(dto.getDataScope());
        }

        saveOrUpdate(entity);
    }

    @Override
    public void initConfig(PermissionQuery query) {

        LambdaQueryWrapper<ContentFormatterConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContentFormatterConfigEntity::getOrgId , query.getOrgId()) ;

        ContentFormatterConfigEntity entity = getOne(queryWrapper);

        entity.setOrgId(query.getOrgId());
        entity.setOperatorId(query.getOperatorId());
        entity.setDepartmentId(query.getDepartmentId());

        saveOrUpdate(entity);
    }

}