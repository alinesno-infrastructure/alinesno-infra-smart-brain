package com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.mapper.DocReviewTaskMapper;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.mapper.GeneralAgentTaskMapper;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTaskService;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通用助手任务服务实现类
 */
@Slf4j
@Service
public class DocReviewTaskServiceImpl extends IBaseServiceImpl<DocReviewTaskEntity, DocReviewTaskMapper> implements IDocReviewTaskService {

    @Override
    public List<DocReviewTaskEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId) {

        page.setPageNum(0);
        page.setPageSize(20);

        Page<DocReviewTaskEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<DocReviewTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewTaskEntity::getOrgId, query.getOrgId()) ;
        wrapper.eq(DocReviewTaskEntity::getSceneId, sceneId);
        wrapper.orderByDesc(DocReviewTaskEntity::getAddTime) ;

        pageBean = page(pageBean, wrapper);

        return pageBean.getRecords();
    }

}