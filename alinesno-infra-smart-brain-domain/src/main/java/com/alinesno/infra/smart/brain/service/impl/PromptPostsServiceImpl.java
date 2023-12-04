package com.alinesno.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.brain.entity.PromptPostsEntity;
import com.alinesno.infra.smart.brain.mapper.PromptPostsMapper;
import com.alinesno.infra.smart.brain.service.IPromptPostsService;
import com.alinesno.infra.smart.brain.utils.PromptUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class PromptPostsServiceImpl extends IBaseServiceImpl<PromptPostsEntity, PromptPostsMapper> implements IPromptPostsService {

    private static final Gson gson = new Gson() ;

    @Override
    public void updatePromptContent(List<PromptMessageDto> messageDto, String postId) {

        PromptPostsEntity e = getById(postId) ;
        e.setPromptContent(gson.toJson(messageDto));

        this.update(e) ;
    }

    @Override
    public void savePromptPost(PromptPostsEntity entity) {

        // 生成调用唯一码
        entity.setPromptId(PromptUtils.generateShortUuid());

        this.save(entity) ;
    }

    @Override
    public PromptPostsEntity getByPromptId(String promptId) {

        LambdaQueryWrapper<PromptPostsEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(PromptPostsEntity::getPromptId, promptId) ;

        return this.getOne(wrapper);
    }

}
