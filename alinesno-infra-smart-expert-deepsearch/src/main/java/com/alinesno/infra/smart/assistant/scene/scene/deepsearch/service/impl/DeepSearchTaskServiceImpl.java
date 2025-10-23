package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.mapper.DeepSearchTaskMapper;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskService;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 深度搜索场景服务实现类
 */
@Slf4j
@Service
public class DeepSearchTaskServiceImpl extends IBaseServiceImpl<DeepSearchTaskEntity, DeepSearchTaskMapper> implements IDeepSearchTaskService {

    @Override
    public void updateTitleAndDescById(DeepSearchTaskEntity taskEntity, String title, String desc) {

        if (taskEntity.getId() == null) {
            log.error("更新失败：任务ID不能为空");
            throw new IllegalArgumentException("任务ID不能为空");
        }

        baseMapper.updateTitleAndDescById(title , desc , taskEntity.getId()) ;

        log.info("已更新ID为{}的任务的名称和描述", taskEntity.getId());
    }
}
