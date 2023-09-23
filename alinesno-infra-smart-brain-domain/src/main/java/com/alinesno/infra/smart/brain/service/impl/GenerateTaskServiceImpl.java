package com.alinesno.infra.smart.brain.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.mapper.GenerateTaskMapper;
import com.alinesno.infra.smart.brain.scheduler.TaskProcessor;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GenerateTaskServiceImpl extends IBaseServiceImpl<GenerateTaskEntity, GenerateTaskMapper> implements IGenerateTaskService {

    @Autowired
    private TaskProcessor taskProcessor ;

    @Override
    public void commitTask(BrainTaskDto dto) {

        GenerateTaskEntity entity = new GenerateTaskEntity() ;
        BeanUtils.copyProperties(dto , entity);

        this.save(entity) ;

        taskProcessor.addTaskToDisruptor(entity);
    }

    @Override
    public void commitTaskToCms(BrainTaskDto dto) {

        GenerateTaskEntity entity = new GenerateTaskEntity() ;
        BeanUtils.copyProperties(dto , entity);

        this.save(entity) ;
    }

    @Override
    public List<GenerateTaskEntity> getAllUnfinishedTasks() {

        QueryWrapper<GenerateTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(GenerateTaskEntity::getTaskStatus, TaskStatus.QUEUED.getValue(), TaskStatus.RUNNING.getValue(), TaskStatus.FAILED.getValue());

        return list(queryWrapper);
    }
}
