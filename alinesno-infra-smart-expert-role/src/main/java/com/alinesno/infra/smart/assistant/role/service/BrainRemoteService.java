package com.alinesno.infra.smart.assistant.role.service;

import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.api.reponse.TaskContentDto;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 调用远程服务
 */
@Slf4j
@Service
public class BrainRemoteService {

    @Autowired
    private IGenerateTaskService generateTaskService ;

    /**
     * 提交任务
     * @param params
     * @param businessId
     * @param promptId
     */
    @Retryable(retryFor = {Exception.class} , maxAttempts = 5 , backoff = @Backoff(delay = 5000L , multiplier = 2))
    public void chatTask(Map<String , Object> params , String businessId , String promptId){

        BrainTaskDto dto = new BrainTaskDto() ;

        dto.setPromptId(promptId);
//        dto.setBusinessId(businessId);
        dto.setParams(params);

        String backBusinessId = generateTaskService.commitTask(dto) ;
        log.debug("businessId = {}" , backBusinessId);
    }

    /**
     * 获取内容服务
     * @param businessId
     * @return
     */
    @Retryable(retryFor = {RuntimeException.class} , maxAttempts = 5 , backoff = @Backoff(delay = 5000L , multiplier = 1.5))
    public TaskContentDto chatContent(String businessId) {

        try{
            TaskContentDto result = generateTaskService.getContentByBusinessId(businessId) ;

            log.debug("chatContent result = {}" , result);

            return result ;
        }catch (Exception e){
            throw new RuntimeException("远程请求异常:" + e.getMessage()) ;
        }

    }

}
