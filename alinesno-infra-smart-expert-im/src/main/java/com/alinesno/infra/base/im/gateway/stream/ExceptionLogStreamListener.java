package com.alinesno.infra.base.im.gateway.stream;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

/**
 * 异常日志流监听器
 */
@Slf4j
@Component
public class ExceptionLogStreamListener implements ApplicationRunner, DisposableBean {

    @Resource
    RedisConnectionFactory redisConnectionFactory;

    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    ListenerStreamMessage streamMessageListener;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer ;

    /**
     * 这里必须先判空，重复创建组会报错，获取不存在的key的组也会报错
     * 所以需要先判断是否存在key，在判断是否存在组
     * 我这里只有一个组，如果需要创建多个组的话则需要改下逻辑
     */
    @Override
    public void run(ApplicationArguments args) throws UnknownHostException {

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(StreamConstants.IM_TASK_LOG))) {
            StreamInfo.XInfoGroups groups = stringRedisTemplate.opsForStream().groups(StreamConstants.IM_TASK_LOG);
            if (groups.isEmpty()) {
                stringRedisTemplate.opsForStream().createGroup(StreamConstants.IM_TASK_LOG, StreamConstants.IM_GROUP_MESSAGE);
            }
        } else {
            stringRedisTemplate.opsForStream().createGroup(StreamConstants.IM_TASK_LOG, StreamConstants.IM_GROUP_MESSAGE);
        }

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(StreamConstants.IM_MESSAGE_LOG))) {
            StreamInfo.XInfoGroups groups = stringRedisTemplate.opsForStream().groups(StreamConstants.IM_MESSAGE_LOG);
            if (groups.isEmpty()) {
                stringRedisTemplate.opsForStream().createGroup(StreamConstants.IM_MESSAGE_LOG, StreamConstants.IM_GROUP_MESSAGE);
            }
        } else {
            stringRedisTemplate.opsForStream().createGroup(StreamConstants.IM_MESSAGE_LOG, StreamConstants.IM_GROUP_MESSAGE);
        }

        // 创建配置对象
        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>>
                streamMessageListenerContainerOptions = StreamMessageListenerContainerOptions
                .builder()
                .batchSize(500) // 一次性最多拉取多少条消息
                .executor(this.threadPoolTaskExecutor) // 执行消息轮询的执行器
                .errorHandler(new ErrorHandler() { // 消息消费异常的handler
                    @Override
                    public void handleError(Throwable t) {
                        t.printStackTrace();
                        log.error("[MQ handler exception] " + t.getMessage());
                    }
                })
                // 超时时间，设置为0，表示不超时（超时后会抛出异常）
                .pollTimeout(Duration.ZERO)
                // 序列化器
                .serializer(new StringRedisSerializer())
                .build();

        // 根据配置对象创建监听容器对象
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer = StreamMessageListenerContainer
                .create(this.redisConnectionFactory);

        // 使用监听容器对象开始监听消费（使用的是手动确认方式）
        streamMessageListenerContainer.receive(Consumer.from(StreamConstants.IM_GROUP_MESSAGE, InetAddress.getLocalHost().getHostName()),
                StreamOffset.create(StreamConstants.IM_TASK_LOG, ReadOffset.lastConsumed()), this.streamMessageListener);

        streamMessageListenerContainer.receive(Consumer.from(StreamConstants.IM_GROUP_MESSAGE, InetAddress.getLocalHost().getHostName()),
                StreamOffset.create(StreamConstants.IM_MESSAGE_LOG, ReadOffset.lastConsumed()), this.streamMessageListener);

        this.streamMessageListenerContainer = streamMessageListenerContainer ;

        // 启动监听
        streamMessageListenerContainer.start();

    }

    @Override
    public void destroy() throws Exception {
        streamMessageListenerContainer.stop();
    }
}
