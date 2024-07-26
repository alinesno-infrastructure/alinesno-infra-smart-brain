package com.alinesno.infra.smart.assistant.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis消息收发配置
 */
@Configuration
public class RedisConfig {

    @Value("${alinesno.infra.smart.assistant.topic:chain_rule_topic}")
    private String ruleTopic;

    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory factory,
                                                           MessageListenerAdapter messageListenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic(ruleTopic));
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisReceiver redisReceiver){
        return new MessageListenerAdapter(redisReceiver,  "onMessage");
    }
}
