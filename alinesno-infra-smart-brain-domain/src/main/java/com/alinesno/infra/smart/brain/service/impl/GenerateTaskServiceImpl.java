package com.alinesno.infra.smart.brain.service.impl;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GenerateTaskServiceImpl extends IBaseServiceImpl<GenerateTaskEntity, GenerateTaskMapper> implements IGenerateTaskService {

    @Value("${alinesno.infra.smart.brain.max-retry:5}")
    private int maxRetryCount ;

    @Autowired
    private IFileProcessingService fileProcessingService ;

//    @Autowired
//    private IPromptPostsService postsServiVkce ;

    @Override
    public String commitTask(BrainTaskDto dto) {

//        GenerateTaskEntity entity = new GenerateTaskEntity() ;
//        BeanUtils.copyProperties(dto , entity);
//        entity.setBusinessId(IdUtil.getSnowflakeNextIdStr());
//
//        // 获取到PromptPost内容
//        PromptPostsEntity postsEntity = postsService.getByPromptId(dto.getPromptId()) ;
//        Assert.notNull(postsEntity , "未找到PromptPost内容");
//
//        entity.setTaskDesc(postsEntity.getPromptName());
//
//        entity.setParams(JSONObject.toJSONString(dto.getParams()));
//        entity.setTaskStatus(TaskStatus.RUNNING.getValue());
//        entity.setUpdateTime(new Date());
//
//        this.save(entity) ;
//
//        fileProcessingService.processFile(entity);
//
//        return entity.getBusinessId() ;

        return null ;
    }

    @Override
    public void commitTaskToCms(BrainTaskDto dto) {

        GenerateTaskEntity entity = new GenerateTaskEntity() ;
        BeanUtils.copyProperties(dto , entity);

        this.save(entity) ;
    }

    @Override
    public List<GenerateTaskEntity> getAllUnfinishedTasks(int retryCount) {

        QueryWrapper<GenerateTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .le(GenerateTaskEntity::getRetryCount , retryCount)
                .in(GenerateTaskEntity::getTaskStatus, TaskStatus.QUEUED.getValue(), TaskStatus.FAILED.getValue());

        return list(queryWrapper);
    }

//    @Override
//    public TaskContentDto getContentByBusinessId(String businessId) {
//
//        LambdaQueryWrapper<GenerateTaskEntity> wrapper = new LambdaQueryWrapper<>() ;
//        wrapper.eq(GenerateTaskEntity::getBusinessId , businessId)  ;
//
//        GenerateTaskEntity generateTask = this.getOne(wrapper) ;
//        // 封装成DTO类对象
//        TaskContentDto dto = new TaskContentDto() ;
//
//        if(generateTask == null){  // 数据内容为空
//            dto.setTaskStatus(TaskStatus.RUNNING.getValue()); // 数据还在生成中
//            return dto ;
//        }
//
//
//        dto.setTaskStatus(generateTask.getTaskStatus());
//        dto.setBusinessId(businessId);
//        dto.setGenContent(generateTask.getAssistantContent());
//
//        List<TaskContentDto.CodeContent> codeContents = CodeBlockParser.parseCodeBlocks(generateTask.getAssistantContent())  ;
//        dto.setCodeContent(codeContents);
//
//        return dto ;
//    }

    @Override
    public void resetRetry(Long taskId) {
       GenerateTaskEntity task = this.getById(taskId)  ;

       task.setRetryCount(0);
       task.setTaskStatus(TaskStatus.RUNNING.getValue());

       this.update(task) ;

        fileProcessingService.processFile(task);
    }

//    @Override
//    public void modifyContent(TaskContentDto dto) {
//
//       LambdaQueryWrapper<GenerateTaskEntity> wrapper = new LambdaQueryWrapper<>() ;
//       wrapper.eq(GenerateTaskEntity::getBusinessId , dto.getBusinessId()) ;
//       wrapper.orderByDesc(GenerateTaskEntity::getAddTime) ;
//       wrapper.last("limit 1") ;
//
//       GenerateTaskEntity task = getOne(wrapper) ;
//
//       if(task != null){
//           task.setAssistantContent(dto.getGenContent());
//           update(task);
//       }
//
//    }

    @Override
    public List<GenerateTaskEntity> getAllUnstableTasks(int jobOutTime) {
        QueryWrapper<GenerateTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(GenerateTaskEntity::getUpdateTime , DateAddInterval(new Date(), - jobOutTime))
                .eq(GenerateTaskEntity::getTaskStatus, TaskStatus.RUNNING.getValue()) ;

        return list(queryWrapper);
    }

    @Override
    public void resetRetryTask(GenerateTaskEntity dto) {

        log.debug("任务:{}，业务bId:{},重试次数:{}" , dto.getTaskDesc() , dto.getBusinessId() , dto.getRetryCount());

        if(dto.getRetryCount() > maxRetryCount){
            dto.setRetryCount(dto.getRetryCount()+1);
            dto.setTaskStatus(TaskStatus.RUNNING.getValue());
            update(dto);
            fileProcessingService.processFile(dto);
        }else {
            dto.setRetryCount(dto.getRetryCount());
            dto.setTaskStatus(TaskStatus.FAILED.getValue());
            update(dto);
        }

    }

    // 辅助方法，根据当前时间和指定秒数计算时间间隔
    private Date DateAddInterval(Date currentDate, int seconds) {
        long currentMilliseconds = currentDate.getTime();
        long intervalMilliseconds = seconds * 1000L;
        return new Date(currentMilliseconds + intervalMilliseconds);
    }
}
