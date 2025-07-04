package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.mapper.GeneralAgentPlanMapper;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.mapper.GeneralAgentTaskMapper;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTaskService;
import com.alinesno.infra.smart.scene.entity.GeneralAgentPlanEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTaskEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通用助手任务服务实现类
 */
@Slf4j
@Service
public class GeneralAgentTaskServiceImpl extends IBaseServiceImpl<GeneralAgentTaskEntity, GeneralAgentTaskMapper> implements IGeneralAgentTaskService {

    @Autowired
    private GeneralAgentPlanMapper generalAgentPlanMapper;

    @Override
    public List<GeneralAgentTaskEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId) {

        page.setPageNum(0);
        page.setPageSize(20);

        Page<GeneralAgentTaskEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<GeneralAgentTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GeneralAgentTaskEntity::getOrgId, query.getOrgId()) ;
        wrapper.eq(GeneralAgentTaskEntity::getSceneId, sceneId);
        wrapper.orderByDesc(GeneralAgentTaskEntity::getAddTime) ;

        pageBean = page(pageBean, wrapper);

        List<GeneralAgentTaskEntity> records = pageBean.getRecords();

        // 处理desc描述字段，获取第一章节的描述字段
        for (GeneralAgentTaskEntity entity : records) {
            LambdaQueryWrapper<GeneralAgentPlanEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(GeneralAgentPlanEntity::getTaskId, entity.getId()) ;
            List<GeneralAgentPlanEntity> chapter = generalAgentPlanMapper.selectList(lambdaQueryWrapper) ;
            if(chapter != null && !chapter.isEmpty()){
                entity.setTaskDescription(chapter.get(0).getContent());
            }else{
                // 有一段说明缺少
                entity.setTaskDescription("暂时未执行任务，未存在任务描述.");
            }
        }

        return records ;

    }

}