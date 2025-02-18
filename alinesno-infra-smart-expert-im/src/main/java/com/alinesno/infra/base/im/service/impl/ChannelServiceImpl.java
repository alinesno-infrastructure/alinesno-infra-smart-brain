package com.alinesno.infra.base.im.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.im.mapper.ChannelMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.enums.HasDeleteEnums;
import com.alinesno.infra.common.facade.enums.HasStatusEnums;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.constants.ImConstants;
import com.alinesno.infra.smart.im.entity.AccountChannelEntity;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.entity.ChannelRoleEntity;
import com.alinesno.infra.smart.im.enums.ChannelType;
import com.alinesno.infra.smart.im.service.IAccountChannelService;
import com.alinesno.infra.smart.im.service.IChannelRoleService;
import com.alinesno.infra.smart.im.service.IChannelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
public class ChannelServiceImpl extends IBaseServiceImpl<ChannelEntity, ChannelMapper> implements IChannelService {

    @Autowired
    private IAccountChannelService accountChannelService ;

    @Autowired
    private IChannelRoleService channelRoleService ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private BaseSearchConsumer baseSearchConsumer; ;

    @Override
    public void initPersonChannel(long accountId) {

        LambdaQueryWrapper<ChannelEntity> queryPersonPublicWrapper = new LambdaQueryWrapper<>() ;
        queryPersonPublicWrapper
                .eq(ChannelEntity::getHasStatus, HasStatusEnums.LEGAL.value)
                .eq(ChannelEntity::getOperatorId , accountId)
                .eq(ChannelEntity::getChannelType , ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue());

        List<ChannelEntity> personPublicChannel = list(queryPersonPublicWrapper) ;

        if(personPublicChannel.isEmpty()){
            ChannelEntity e = new ChannelEntity() ;
            e.setId(accountId);

            e.setChannelName("个人公共频道");
            e.setChannelDesc("公共频道服务，我的个人公共频道！这是一个开放且充满活力的空间，旨在为所有对技术、创新、生活方式以及个人成长，用于公共交流");
            e.setChannelType(ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue());

            e.setHasStatus(HasStatusEnums.LEGAL.value);
            e.setIcon(ImConstants.DEFAULT_AVATAR) ;
            e.setChannelId(IdUtil.nanoId(8));

            e.setOperatorId(accountId);

            // 创建频道知识库
            R<Long> createDataset = baseSearchConsumer.datasetCreate(e.getChannelDesc() , e.getChannelName() ,
                    e.getOrgId() + "" ,
                    e.getOperatorId() + "") ;
            e.setKnowledgeId(createDataset.getData()+"");

            try {
                this.saveOrUpdate(e);
            }catch(Exception ex) {
                log.error("创建个人用户频道异常:{}", ex.getMessage());
            }
        }
    }

    @Override
    public String createChannel(ChannelEntity entity) {

        entity.setChannelId(IdUtil.getSnowflakeNextIdStr());
        entity.setChannelType(entity.getChannelType()) ;

        // 创建频道知识库
        R<Long> createDataset = baseSearchConsumer.datasetCreate(entity.getChannelDesc() ,
                entity.getChannelName() ,
                entity.getOrgId()+"" ,
                entity.getOperatorId()+"") ;
        entity.setKnowledgeId(createDataset.getData()+"");

        this.save(entity) ;

        return entity.getId()+"";
    }

    @Override
    public void removeChannel(Long channelId) {
        LambdaUpdateWrapper<ChannelEntity> updateWrapper = new LambdaUpdateWrapper<>() ;

        updateWrapper.eq(ChannelEntity::getId ,  channelId)
                .set(ChannelEntity::getHasDelete , HasDeleteEnums.ILLEGAL.value)
                .notIn(ChannelEntity::getChannelType , ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue());

        this.update(updateWrapper) ;
    }

    @Override
    public List<ChannelEntity> allMyChannel(PermissionQuery query) {

        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.setEntityClass(ChannelEntity.class) ;
        query.toWrapper(queryWrapper);
        queryWrapper.eq(ChannelEntity::getHasDelete , HasDeleteEnums.LEGAL.value) ;
        queryWrapper.orderByDesc(ChannelEntity::getAddTime) ;

        return list(queryWrapper);
    }

    @Override
    public List<ChannelEntity> allPublicChannel(PermissionQuery query) {

        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.setEntityClass(ChannelEntity.class) ;
        query.toWrapper(queryWrapper);
        queryWrapper.eq(ChannelEntity::getHasDelete , HasDeleteEnums.LEGAL.value) ;

        return list(queryWrapper) ;
    }

    @Override
    public void jobChannel(long roleId, long channelId) {
        log.debug("userId = {} , channelId = {}" , roleId , channelId);

        LambdaQueryWrapper<ChannelRoleEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(ChannelRoleEntity::getChannelId , channelId)
                .eq(ChannelRoleEntity::getAccountType, roleId) ;

        long count = channelRoleService.count(wrapper) ;
        Assert.isTrue(count == 0 , "角色已经在频道里面");

        ChannelRoleEntity channelUser = new ChannelRoleEntity() ;

        channelUser.setAccountType("agent");
        channelUser.setChannelId(channelId);
        channelUser.setAccountId(roleId);

        channelRoleService.save(channelUser) ;
    }

    @Override
    public Long getDefaultChannelId(long userId) {

        List<ChannelEntity> list = list(new LambdaQueryWrapper<ChannelEntity>().eq(ChannelEntity::getOperatorId, userId)) ;
        long channelId = 0L;

        for(ChannelEntity e : list){
            if(e.getChannelType().equals(ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue())){
                channelId = e.getId() ;
            }
        }

        return channelId;
    }

    @Override
    public List<ChannelEntity> getRecommendChannel() {
        log.debug("获取推荐频道");

        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.eq(ChannelEntity::getChannelType, ChannelType.PUBLIC_CHANNEL.getValue())
                .last("limit 5");

        return list(queryWrapper);

    }

    @Override
    public void accountJoinChannel(long accountId, long channelId) {

        LambdaUpdateWrapper<AccountChannelEntity> updateWrapper = new LambdaUpdateWrapper<>() ;
        updateWrapper
                .eq(AccountChannelEntity::getAccountId , accountId)
                .eq(AccountChannelEntity::getChannelId , channelId) ;

        if(accountChannelService.count(updateWrapper) > 0){
            log.debug("用户已经加入该频道");
        }else{
            log.debug("用户没有加入该频道，开始加入");
            AccountChannelEntity accountChannelEntity = new AccountChannelEntity() ;

            accountChannelEntity.setAccountId(accountId);
            accountChannelEntity.setChannelId(channelId);

            accountChannelService.save(accountChannelEntity);
        }

    }

    @Override
    public void batchCreateChannel(List<ChannelEntity> recommendChannels) {
        // 判断是否为空，不为空则输出warning
        if (recommendChannels.isEmpty()) {
            log.warn("传入的实体列表为空，无法创建角色");
            return;
        }

        // 创建角色知识库
        for (ChannelEntity channel : recommendChannels) {
            // TODO 待集成批量添加知识库
            R<Long> result = baseSearchConsumer.datasetCreate(channel.getChannelDesc(), channel.getChannelName() , channel.getOrgId() +"" , channel.getOperatorId() +"");
            log.debug("创建知识库结果：" + result);
            channel.setKnowledgeId(result.getData()+"");
        }

        // 先保存用户信息
        this.saveOrUpdateBatch(recommendChannels);
    }

    @Override
    public void initOrgChannel(long orgId) {
        // 如果当前组织的频道为空时，会默认初始化一个频道，用于演示
        List<ChannelEntity> list = list(new LambdaQueryWrapper<ChannelEntity>().eq(ChannelEntity::getOrgId, orgId));

        if(list.isEmpty()){
            ChannelEntity channelEntity = new ChannelEntity() ;

            channelEntity.setOrgId(orgId);
            channelEntity.setChannelType(ChannelType.PUBLIC_CHANNEL.getValue());
            channelEntity.setChannelName("默认频道");
            channelEntity.setChannelDesc("默认频道是用户与系统或其他用户进行初次互动的场所，这里可以查看通用信息或开始创建自己的第一个频道。");
            channelEntity.setChannelKey("default");
            channelEntity.setKnowledgeType("");
            channelEntity.setKnowledgeId("");
            channelEntity.setIcon("1851897451138109441");
            channelEntity.setChannelId(IdUtil.getSnowflakeNextIdStr());

            save(channelEntity);
        }
    }

    @Override
    public boolean hasRole(long orgId) {
        long count = roleService.count(new LambdaQueryWrapper<IndustryRoleEntity>().eq(IndustryRoleEntity::getOrgId, orgId));
        return count != 0 ;
    }

}
