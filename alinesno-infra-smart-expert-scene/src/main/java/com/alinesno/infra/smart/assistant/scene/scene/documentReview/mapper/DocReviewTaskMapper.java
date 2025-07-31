package com.alinesno.infra.smart.assistant.scene.scene.documentReview.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档审核任务实体对象
 */
@Mapper
public interface DocReviewTaskMapper extends IBaseMapper<DocReviewTaskEntity> {
}    