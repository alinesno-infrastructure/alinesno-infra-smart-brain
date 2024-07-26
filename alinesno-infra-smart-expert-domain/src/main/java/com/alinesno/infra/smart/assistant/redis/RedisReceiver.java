package com.alinesno.infra.smart.assistant.redis;

import com.alinesno.infra.smart.assistant.chain.IChainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis消息接收
 */
@Component
@Slf4j
public class RedisReceiver implements MessageListener {

    @Autowired
    private IChainService chainService ;

    @Override
    public void onMessage(Message message, byte[] bytes) {

        if(MessageConstants.RELOAD_RULE.equals(message.toString())){
            chainService.reloadRule() ;
        }

    }
}
