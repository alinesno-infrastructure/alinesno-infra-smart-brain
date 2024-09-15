package com.alinesno.infra.base.im.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.im.mapper.ChannelMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.enums.HasDeleteEnums;
import com.alinesno.infra.common.facade.enums.HasStatusEnums;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.BaseSearchConsumer;
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
            e.setChannelDesc("公共频道服务，用于公共交流");
            e.setChannelType(ChannelType.PERSONAL_PUBLIC_CHANNEL.getValue());

            e.setHasStatus(HasStatusEnums.LEGAL.value);
            e.setIcon(ImConstants.DEFAULT_AVATAR) ;
            e.setChannelId(IdUtil.nanoId(8));

            e.setOperatorId(accountId);

            try {
                this.saveOrUpdate(e);
            }catch(Exception ex) {
                log.error("创建个人用户频道异常:{}", ex.getMessage());
            }
        }
    }

    @Override
    public String createChannel(ChannelEntity entity) {

        entity.setChannelId(IdUtil.nanoId());
        entity.setChannelType(entity.getChannelType()) ;

        // 创建频道知识库
        R<String> createDataset = baseSearchConsumer.datasetCreate(entity.getChannelId() , entity.getChannelName()) ;
        entity.setKnowledgeId(createDataset.getData());

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
    public List<ChannelEntity> allMyChannel() {

        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>() ;
        queryWrapper.eq(ChannelEntity::getHasDelete , HasDeleteEnums.LEGAL.value) ;

        return list(queryWrapper);
    }

    @Override
    public List<ChannelEntity> allPublicChannel() {

        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>() ;
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
    public Long getDefaultChannelId() {

        List<ChannelEntity> list = allMyChannel() ;
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
            R<String> result = baseSearchConsumer.datasetCreate(channel.getChannelDesc(), channel.getChannelName());
            log.debug("创建知识库结果：" + result);
            channel.setKnowledgeId(result.getData());
        }

        // 先保存用户信息
        this.saveOrUpdateBatch(recommendChannels);
    }

}
