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
        // 1. 验证必要参数（ID 不能为空）
        if (taskEntity.getId() == null) {
            log.error("更新失败：任务ID不能为空");
            throw new IllegalArgumentException("任务ID不能为空");
        }

        // 2. 构建更新条件（where id = ?）
        LambdaUpdateWrapper<DeepSearchTaskEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DeepSearchTaskEntity::getId, taskEntity.getId());

        // 3. 构建更新内容（只更新 taskName 和 taskDescription 字段）
        DeepSearchTaskEntity updateEntity = new DeepSearchTaskEntity();
        updateEntity.setTaskName(title);       // 更新任务名称（title）
        updateEntity.setTaskDescription(desc); // 更新任务描述（desc）

        // 4. 执行更新操作
        baseMapper.update(updateEntity, updateWrapper);

        log.info("已更新ID为{}的任务的名称和描述", taskEntity.getId());
    }
}
