package com.alinesno.infra.smart.brain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.mapper.GenerateTaskMapper;
import com.alinesno.infra.smart.brain.service.IFileProcessingService;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GenerateTaskServiceImpl extends IBaseServiceImpl<GenerateTaskEntity, GenerateTaskMapper> implements IGenerateTaskService {

    @Autowired
    private IFileProcessingService fileProcessingService ;

    @Override
    public Long commitTask(BrainTaskDto dto) {

        GenerateTaskEntity entity = new GenerateTaskEntity() ;
        BeanUtils.copyProperties(dto , entity);
        entity.setBusinessId(IdUtil.getSnowflakeNextIdStr());

        // 获取到PromptPost内容
//        PromptPostsEntity postsEntity = postsService.getByPromptId(dto.getRoleId()) ;
//        Assert.notNull(postsEntity , "未找到PromptPost内容");
//
//        entity.setTaskDesc(postsEntity.getPromptName());
//
//        entity.setParams(JSONObject.toJSONString(dto.getParams()));
        entity.setTaskStatus(TaskStatus.RUNNING.getValue());
        entity.setUpdateTime(new Date());

        this.save(entity) ;

        fileProcessingService.processFile(entity);

        return entity.getId() ;
    }

    @Override
    public List<GenerateTaskEntity> getAllUnfinishedTasks(int retryCount) {

        QueryWrapper<GenerateTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .le(GenerateTaskEntity::getRetryCount , retryCount)
                .in(GenerateTaskEntity::getTaskStatus, TaskStatus.QUEUED.getValue(), TaskStatus.FAILED.getValue());

        return list(queryWrapper);
    }


    @Override
    public List<GenerateTaskEntity> getAllUnstableTasks(int jobOutTime) {
        QueryWrapper<GenerateTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(GenerateTaskEntity::getUpdateTime , DateAddInterval(new Date(), - jobOutTime))
                .eq(GenerateTaskEntity::getTaskStatus, TaskStatus.RUNNING.getValue()) ;

        return list(queryWrapper);
    }

    // 辅助方法，根据当前时间和指定秒数计算时间间隔
    private Date DateAddInterval(Date currentDate, int seconds) {
        long currentMilliseconds = currentDate.getTime();
        long intervalMilliseconds = seconds * 1000L;
        return new Date(currentMilliseconds + intervalMilliseconds);
    }
}
