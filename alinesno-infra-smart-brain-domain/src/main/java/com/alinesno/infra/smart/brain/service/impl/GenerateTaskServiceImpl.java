package com.alinesno.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.enums.TaskStatus;
import com.alinesno.infra.smart.brain.mapper.GenerateTaskMapper;
import com.alinesno.infra.smart.brain.scheduler.TaskProcessor;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import com.alinesno.infra.smart.brain.utils.CodeBlockParser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
public class GenerateTaskServiceImpl extends IBaseServiceImpl<GenerateTaskEntity, GenerateTaskMapper> implements IGenerateTaskService {

    @Autowired
    private TaskProcessor taskProcessor ;

    @Override
    public void commitTask(BrainTaskDto dto) {

        // 判断业务ID是否存在
        String bId = dto.getBusinessId() ;
        Assert.notNull(bId , "业务ID为空") ;

        LambdaQueryWrapper<GenerateTaskEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(GenerateTaskEntity::getBusinessId , bId)  ;

        long count = this.count(wrapper) ;
        Assert.isTrue(count == 0 , "业务ID["+bId+"]已存在");

        GenerateTaskEntity entity = new GenerateTaskEntity() ;
        BeanUtils.copyProperties(dto , entity);

        entity.setTaskStatus(TaskStatus.RUNNING.getValue());

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

    @Override
    public TaskContentDto getContentByBusinessId(String businessId) {

        LambdaQueryWrapper<GenerateTaskEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(GenerateTaskEntity::getBusinessId , businessId)  ;

        GenerateTaskEntity generateTask = this.getOne(wrapper) ;

        // 封装成DTO类对象
        TaskContentDto dto = new TaskContentDto() ;

        dto.setTaskStatus(generateTask.getTaskStatus());
        dto.setBusinessId(businessId);
        dto.setGenContent(generateTask.getAssistantContent());

        List<TaskContentDto.CodeContent> codeContents = CodeBlockParser.parseCodeBlocks(generateTask.getAssistantContent())  ;
        dto.setCodeContent(codeContents);

        return dto ;
    }
}
