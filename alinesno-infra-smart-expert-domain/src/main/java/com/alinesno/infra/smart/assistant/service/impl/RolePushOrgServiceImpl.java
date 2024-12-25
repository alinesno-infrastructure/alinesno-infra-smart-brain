package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.RolePushOrgEntity;
import com.alinesno.infra.smart.assistant.mapper.IndustryRoleMapper;
import com.alinesno.infra.smart.assistant.mapper.RolePushOrgMapper;
import com.alinesno.infra.smart.assistant.service.IRolePushOrgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.Date;
import java.util.List;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class RolePushOrgServiceImpl extends IBaseServiceImpl<RolePushOrgEntity, RolePushOrgMapper> implements IRolePushOrgService {

    @Autowired
    private IndustryRoleMapper industryRoleMapper ;

    /**
     * 推送组织角色，当前标识为未录用，如果同一角色，版本号会加1，并且前面的标识为已录用
     */
    @Override
    public void pushOrgRole(long roleId , long orgId){
        // 1. 查询当前角色是否已经被推送过给过当前组织
        LambdaQueryWrapper<RolePushOrgEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePushOrgEntity::getRoleId, roleId);
        wrapper.eq(RolePushOrgEntity::getOrgId, orgId);

        List<RolePushOrgEntity> list = list(wrapper);
        if(list != null){
            // 2. 如果已经被推送过，则更新当前角色的版本号，前面已经存在的标识为已录用，当前最新的标识为未录用
            LambdaUpdateWrapper<RolePushOrgEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RolePushOrgEntity::getRoleId, roleId);
            updateWrapper.eq(RolePushOrgEntity::getOrgId, orgId);
            updateWrapper.set(RolePushOrgEntity::getHasExpired, 1);  // 角色设置为过期
            update(updateWrapper);
        }

        // 保存新的推送记录
        RolePushOrgEntity rolePushOrgEntity = new RolePushOrgEntity();
        rolePushOrgEntity.setRoleId(roleId);
        rolePushOrgEntity.setOrgId(orgId);
        rolePushOrgEntity.setAccepted(false);
        rolePushOrgEntity.setHasExpired(0);
        rolePushOrgEntity.setVersion(list == null ? 0 : list.size() + 1);
        save(rolePushOrgEntity);
    }

    /**
     * 录用组织角色，录用之后，原角色信息会被覆盖，然后当前角色标识为已录用
     */
    @Override
    public void acceptOrgRole(long roleId , long orgId){

        // 查询出当前版本号最高的角色，然后进行录用
        LambdaQueryWrapper<RolePushOrgEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePushOrgEntity::getRoleId, roleId);
        wrapper.eq(RolePushOrgEntity::getOrgId, orgId);
        wrapper.orderByDesc(RolePushOrgEntity::getVersion);

        List<RolePushOrgEntity> list = list(wrapper);
        if(list != null && !list.isEmpty()){
            RolePushOrgEntity rolePushOrgEntity = list.get(0);
            rolePushOrgEntity.setAccepted(true);
            rolePushOrgEntity.setHasExpired(1);
            rolePushOrgEntity.setAcceptTime(new Date());
            updateById(rolePushOrgEntity);
        }else{
            log.error("没有找到角色信息，无法录用");
            throw new RpcServiceRuntimeException("没有找到角色信息，无法录用");
        }

    }

    @Override
    public List<IndustryRoleEntity> queryPushOrgRole(long orgId) {

        // 查询出推送组织下未过期的角色
        LambdaQueryWrapper<RolePushOrgEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePushOrgEntity::getOrgId, orgId);
        wrapper.eq(RolePushOrgEntity::getHasExpired, 0);

        List<RolePushOrgEntity> list = list(wrapper);

        if(list != null && !list.isEmpty()){
            List<Long> roleIds = list.stream().map(RolePushOrgEntity::getRoleId).toList();
            LambdaQueryWrapper<IndustryRoleEntity> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(IndustryRoleEntity::getId, roleIds);
            return industryRoleMapper.selectList(roleWrapper);
        }

        return null;
    }
}