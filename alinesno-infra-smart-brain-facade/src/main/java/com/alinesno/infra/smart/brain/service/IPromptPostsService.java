//package com.alinesno.infra.smart.brain.service;
//
//import com.alinesno.infra.common.facade.services.IBaseService;
//import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
//import com.alinesno.infra.smart.brain.entity.PromptPostsEntity;
//
//import java.util.List;
//
///**
// * 【请填写功能名称】Service接口
// *
// * @author luoxiaodong
// * @version 1.0.0
// */
//public interface IPromptPostsService extends IBaseService<PromptPostsEntity> {
//
//    /**
//     *
//     * @param messageDto
//     * @param postId
//     */
//    void updatePromptContent(List<PromptMessageDto> messageDto, String postId);
//
//    /**
//     *
//     * @param entity
//     */
//    void savePromptPost(PromptPostsEntity entity);
//
//    /**
//     * 通过Pin码查询实体信息
//     * @param promptId
//     * @return
//     */
//    PromptPostsEntity getByPromptId(String promptId);
//
//}