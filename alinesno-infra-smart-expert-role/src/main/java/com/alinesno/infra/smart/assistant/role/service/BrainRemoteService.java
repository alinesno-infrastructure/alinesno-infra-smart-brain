package com.alinesno.infra.smart.assistant.role.service;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.adapter.SmartBrainConsumer;
import com.alinesno.infra.smart.assistant.api.adapter.BrainTaskDto;
import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
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
    private SmartBrainConsumer smartBrainConsumer ;

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
        dto.setBusinessId(businessId);
        dto.setParams(params);

        AjaxResult result = smartBrainConsumer.chatTask(dto) ;
        log.debug("result = {}" , result);
    }

    /**
     * 获取内容服务
     * @param businessId
     * @return
     */
    @Retryable(retryFor = {RuntimeException.class} , maxAttempts = 5 , backoff = @Backoff(delay = 5000L , multiplier = 1.5))
    public TaskContentDto chatContent(String businessId) {

        try{
            AjaxResult result = smartBrainConsumer.chatContent(businessId) ;

            log.debug("chatContent result = {}" , result);

            String resultData = result.get("data").toString() ;
            return JSONObject.parseObject(resultData, TaskContentDto.class);
        }catch (Exception e){
            throw new RuntimeException("远程请求异常:" + e.getMessage()) ;
        }

    }

}
