package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.mapper.DataAnalysisSceneMapper;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.IDataAnalysisSceneService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.scene.dto.RoleListRequestDto;
import com.alinesno.infra.smart.scene.dto.UpdateSceneAgentDto;
import com.alinesno.infra.smart.scene.entity.DataAnalysisSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数据分析场景服务实现类
 */
@Slf4j
@Service
public class DataAnalysisSceneServiceImpl extends IBaseServiceImpl<DataAnalysisSceneEntity, DataAnalysisSceneMapper> implements IDataAnalysisSceneService {

    @Autowired
    private IIndustryRoleService roleService ;

    @Override
    public void updateSceneAgents(UpdateSceneAgentDto dto) {

        long sceneId = dto.getSceneId() ;

        LambdaQueryWrapper<DataAnalysisSceneEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataAnalysisSceneEntity::getSceneId, sceneId) ;
        wrapper.eq(DataAnalysisSceneEntity::getOrgId , dto.getOrgId()) ;
        long count = count(wrapper) ;

        DataAnalysisSceneEntity entity = new DataAnalysisSceneEntity() ;
        if(count > 0){
            entity = getOne(wrapper) ;
        }

        long textParserEngineer = RoleUtils.findSelectAgentIdByCode(dto , "textParser") ;
        long dataMinerEngineer = RoleUtils.findSelectAgentIdByCode(dto , "dataMiner") ;

        entity.setTextParserEngineer(String.valueOf(textParserEngineer)) ;
        entity.setDataMinerEngineer(String.valueOf(dataMinerEngineer)) ;
        entity.setSceneId(sceneId);

        entity.setDepartmentId(dto.getDepartmentId());
        entity.setOrgId(dto.getOrgId());
        entity.setOperatorId(dto.getOperatorId());

        saveOrUpdate(entity) ;

    }

    @Override
    public List<IndustryRoleEntity> getRoleList(RoleListRequestDto dto) {

        long id = dto.getSceneId();

        PermissionQuery query = new PermissionQuery() ;
        BeanUtils.copyProperties(dto , query) ;

        DataAnalysisSceneEntity entity = getBySceneId(id , query);
        if(entity == null){
            return Collections.emptyList() ;
        }

        String agentTypeCode = dto.getAgentTypeCode() ;

        if("textParser".equals(agentTypeCode) && entity.getTextParserEngineer() != null){
            return roleService.listByIds(Arrays.asList(entity.getTextParserEngineer().split(",")));
        }else if("dataMiner".equals(agentTypeCode) && entity.getDataMinerEngineer() != null){
            return roleService.listByIds(Arrays.asList(entity.getDataMinerEngineer().split(",")));
        }

        return Collections.emptyList() ;
    }

    @Override
    public DataAnalysisSceneEntity getBySceneId(long sceneId, PermissionQuery query) {
        ISceneService sceneService = SpringUtil.getBean(ISceneService.class);
        SceneEntity sceneEntity = sceneService.getById(sceneId) ;


        LambdaQueryWrapper<DataAnalysisSceneEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DataAnalysisSceneEntity::getSceneId, sceneId);
        queryWrapper.eq(DataAnalysisSceneEntity::getOrgId , query.getOrgId()) ;

        DataAnalysisSceneEntity entity = getOne(queryWrapper);

        // TODO 待优化逻辑
        // 如果是公共，通过源信息配置，做拦截数据修改更新处理，修改编辑角色信息。
        if(sceneEntity.getSceneScope().equals(SceneScopeType.PUBLIC_SCENE.getValue())){

            LambdaQueryWrapper<DataAnalysisSceneEntity> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(DataAnalysisSceneEntity::getSceneId, sceneId);
            queryWrapper2.eq(DataAnalysisSceneEntity::getOrgId , sceneEntity.getOrgId()) ;
            DataAnalysisSceneEntity orginDataAnalysisSceneEntity = getOne(queryWrapper2) ;

            if(orginDataAnalysisSceneEntity != null){
                if(entity == null) {
                    entity = new DataAnalysisSceneEntity() ;
                    entity.setTextParserEngineer(orginDataAnalysisSceneEntity.getTextParserEngineer()); ;
                    entity.setDataMinerEngineer(orginDataAnalysisSceneEntity.getDataMinerEngineer()); ;
                    entity.setSceneId(sceneId);

                    entity.setDepartmentId(query.getDepartmentId());
                    entity.setOrgId(query.getOrgId());
                    entity.setOperatorId(query.getOperatorId());

                    saveOrUpdate(entity) ;
                }else {
                    if(!entity.getTextParserEngineer().equals(orginDataAnalysisSceneEntity.getTextParserEngineer())  || !entity.getDataMinerEngineer().equals(orginDataAnalysisSceneEntity.getDataMinerEngineer())){
                        entity.setTextParserEngineer(orginDataAnalysisSceneEntity.getTextParserEngineer()) ;
                        entity.setDataMinerEngineer(orginDataAnalysisSceneEntity.getDataMinerEngineer()) ;
                        saveOrUpdate(entity) ;
                    }
                }
            }

        }

        return entity ;
    }

}