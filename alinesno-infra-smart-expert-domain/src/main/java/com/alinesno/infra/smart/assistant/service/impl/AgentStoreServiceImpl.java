package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.pageable.ConditionDto;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.wrapper.RpcWrapper;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.mapper.AgentStoreMapper;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.entity.AgentStoreEntity;
import com.alinesno.infra.smart.im.service.IAgentStoreService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Slf4j
@Service
public class AgentStoreServiceImpl extends IBaseServiceImpl<AgentStoreEntity, AgentStoreMapper> implements IAgentStoreService {

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Override
    public void addRoleToStore(Long roleId , Long agentStoreTypeId, String sceneScope, Long orgId) {

        // 判断角色是否已经在商店里面
        if(roleId != null){
            LambdaQueryWrapper<AgentStoreEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AgentStoreEntity::getAgentId , roleId) ;
            long count = count(wrapper) ;

            if(count > 0){
                AgentStoreEntity agentStoreEntity = getOne(wrapper) ;
                agentStoreEntity.setAgentType(agentStoreTypeId);
                update(agentStoreEntity) ;
            }else{

                AgentStoreEntity agentStoreEntity = new AgentStoreEntity();

                agentStoreEntity.setSortOrder((int) count);
                agentStoreEntity.setAgentId(roleId);
                agentStoreEntity.setRoleOrganizationId(AgentConstants.STORE_EMPTY_ORG_ID);
                agentStoreEntity.setAgentType(agentStoreTypeId);
                agentStoreEntity.setSceneScope(sceneScope);
                agentStoreEntity.setOrgId(orgId);

                save(agentStoreEntity) ;
            }
        }

    }

    @Override
    public List<IndustryRoleEntity> findRoleFromStore(DatatablesPageBean page) {

        String roleName = "" ;

        List<ConditionDto>  conditionList =  page.getConditionList();
        if(conditionList != null && !conditionList.isEmpty()){
           for(ConditionDto conditionDto : conditionList) {
               if("roleName".equals(conditionDto.getColumn())){
                   roleName = conditionDto.getValue() ;
               }
           }
        }

        // 使用 MyBatis-Plus 的分页插件
        if(page.getPageSize() == 0){
            page.setPageSize(20);
        }

        Page<AgentStoreEntity> agentStorePage = new Page<>(page.getPageNum(), page.getPageSize());
        LambdaQueryWrapper<AgentStoreEntity> wrapper = new LambdaQueryWrapper<>();

        // 角色不在任何组织里面的
        wrapper.eq(AgentStoreEntity::getRoleOrganizationId , AgentConstants.STORE_EMPTY_ORG_ID);
        wrapper.orderByDesc(AgentStoreEntity::getSortOrder);

        // 执行分页查询
        Page<AgentStoreEntity> resultPage = page(agentStorePage, wrapper);
        List<AgentStoreEntity> list = resultPage.getRecords();

        // 假设 AgentStoreEntity 中有一个方法可以获取对应的 IndustryRoleEntity 的 ID
        List<Long> roleIds = list.stream()
                .map(AgentStoreEntity::getAgentId) // 这里需要根据实际情况修改为正确的方法
                .toList();

        if(!roleIds.isEmpty()){
            return industryRoleService.listByIds(roleIds);
        }

        return Collections.emptyList() ;
    }

    @Override
    public List<IndustryRoleEntity> findRoleFromStoreByOrgId(DatatablesPageBean page, long orgId) {

        // 使用 MyBatis-Plus 的分页插件
        if(page.getPageSize() == 0){
            page.setPageSize(20);
        }

        Page<AgentStoreEntity> agentStorePage = new Page<>(page.getPageNum(), page.getPageSize());
        LambdaQueryWrapper<AgentStoreEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentStoreEntity::getRoleOrganizationId , orgId);
        wrapper.orderByDesc(AgentStoreEntity::getSortOrder);

        // 执行分页查询
        Page<AgentStoreEntity> resultPage = page(agentStorePage, wrapper);
        List<AgentStoreEntity> list = resultPage.getRecords();

        // 假设 AgentStoreEntity 中有一个方法可以获取对应的 IndustryRoleEntity 的 ID
        List<Long> roleIds = list.stream()
                .map(AgentStoreEntity::getAgentId) // 这里需要根据实际情况修改为正确的方法
                .toList();

        if(!roleIds.isEmpty()){
            return industryRoleService.listByIds(roleIds);
        }

        return Collections.emptyList() ;
    }

    @Override
    public List<IndustryRoleEntity> findRoleBySceneAgentId(DatatablesPageBean page, long sceneAgentId) {
        return null;
    }

    @Override
    public List<IndustryRoleEntity> findRoleBySceneId(DatatablesPageBean page, long sceneId) {
        return null;
    }

    @Override
    public void pushOrgRole(long roleId, long orgId) {
        // 判断角色是否已经在商店里面
        if(roleId != 0){

            // 判断角色是否已经发布在公共商店
            boolean hasPublicStore = hasPublicStore(roleId) ;
            Assert.isTrue(!hasPublicStore , "角色已经发布在公共商店，不能重复推送给单个组织") ;

            LambdaQueryWrapper<AgentStoreEntity> wrapper = new LambdaQueryWrapper<>();

            wrapper.eq(AgentStoreEntity::getAgentId , roleId) ;
            wrapper.eq(AgentStoreEntity::getAgentType , AgentConstants.ORG_AGENT_STORE_TYPE_ID) ;
            wrapper.eq(AgentStoreEntity::getRoleOrganizationId , orgId) ;

            long count = count(wrapper) ;

            if(count == 0){
                AgentStoreEntity agentStoreEntity = new AgentStoreEntity();
                agentStoreEntity.setSortOrder((int) count);
                agentStoreEntity.setAgentId(roleId);
                agentStoreEntity.setRoleOrganizationId(orgId);
                agentStoreEntity.setAgentType(AgentConstants.ORG_AGENT_STORE_TYPE_ID);
                save(agentStoreEntity) ;
            }
        }
    }

    @Override
    public void offlineStore(Long roleId) {
        if(roleId == null){
            return ;
        }
        LambdaQueryWrapper<AgentStoreEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentStoreEntity::getAgentId , roleId) ;
        remove(wrapper) ;
    }

    @Override
    public TableDataInfo storeRoleList(DatatablesPageBean page, Long orgId) {

        Page<AgentStoreEntity> agentStorePage = new Page<>(page.getPageNum(), page.getPageSize());
        LambdaQueryWrapper<AgentStoreEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AgentStoreEntity::getRoleOrganizationId , List.of(AgentConstants.STORE_EMPTY_ORG_ID, orgId)) ;
        wrapper.orderByDesc(AgentStoreEntity::getSortOrder);

        // 执行分页查询
        Page<AgentStoreEntity> resultPage = page(agentStorePage, wrapper);
        List<AgentStoreEntity> list = resultPage.getRecords();

        // 假设 AgentStoreEntity 中有一个方法可以获取对应的 IndustryRoleEntity 的 ID
        List<Long> roleIds = list.stream()
                .map(AgentStoreEntity::getAgentId) // 这里需要根据实际情况修改为正确的方法
                .toList();

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(resultPage.getTotal());

        if(!roleIds.isEmpty()){
            tableDataInfo.setRows(industryRoleService.listByIds(roleIds));
        }

        return tableDataInfo ;

    }

    /**
     * 获取到公共商店角色并且是推荐状态下的Role角色
     * @return
     */
    @Override
    public IndustryRoleEntity getRecommendRole() {
        return mapper.getRecommendRole();
    }

    /**
     * 判断角色是否已经发布在公共商店
     * @param roleId
     * @return 发布返回true，否则返回false
     */
    private boolean hasPublicStore(long roleId) {
        LambdaQueryWrapper<AgentStoreEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentStoreEntity::getAgentId , roleId) ;
        wrapper.eq(AgentStoreEntity::getRoleOrganizationId , 0) ;
        return count(wrapper) > 0 ;
    }

}
