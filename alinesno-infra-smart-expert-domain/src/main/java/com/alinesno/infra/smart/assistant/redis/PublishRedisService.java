package com.alinesno.infra.smart.assistant.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis消息发送
 */
@Service
public class PublishRedisService {

    @Value("${alinesno.infra.smart.assistant.topic:chain_rule_topic}")
    private String ruleTopic;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void sendMsg(String msg){
        stringRedisTemplate.convertAndSend(ruleTopic, msg);
    }

    public void sendMsg(String channel, String msg){
        stringRedisTemplate.convertAndSend(channel, msg);
    }
}
