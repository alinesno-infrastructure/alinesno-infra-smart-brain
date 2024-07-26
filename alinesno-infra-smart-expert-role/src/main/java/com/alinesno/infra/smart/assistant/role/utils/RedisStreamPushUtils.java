package com.alinesno.infra.smart.assistant.role.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisStreamPushUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送任务消息
     * @param msgContext
     */
    public void pushTask(String msgContext){
        stringRedisTemplate.opsForStream().add(Record.of(msgContext).withStreamKey(StreamConstants.IM_TASK_LOG));
        log.info("{}已发送消息:{}", StreamConstants.IM_TASK_LOG,msgContext);
    }

    /**
     * 发送聊天消息
     * @param msgContext
     */
    public void pushMessage(String msgContext){
        stringRedisTemplate.opsForStream().add(Record.of(msgContext).withStreamKey(StreamConstants.IM_MESSAGE_LOG));
        log.info("{}已发送消息:{}", StreamConstants.IM_MESSAGE_LOG,msgContext);
    }

}
