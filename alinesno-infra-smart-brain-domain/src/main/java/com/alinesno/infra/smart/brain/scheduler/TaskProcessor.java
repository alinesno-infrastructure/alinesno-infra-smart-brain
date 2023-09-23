package com.alinesno.infra.smart.brain.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Slf4j
@Component
public class TaskProcessor {

    @Value("${alinesno.infra.smart.brain.disruptor.bufferSize:1024}")
    private int bufferSize;

    private volatile int batchSize;

    public Disruptor<GenerateTaskEntity> disruptor;

    public void init() {
        disruptor = new Disruptor<>(GenerateTaskEntity::new, bufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());

        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            try {
                process(event);
            } catch (Exception e) {
                log.error("Error processing task: {}", event.getBusinessId(), e);
            }
        });

        disruptor.start();
    }

    public void addTaskToDisruptor(GenerateTaskEntity task) {
        long sequence = disruptor.getRingBuffer().next();
        try {
            GenerateTaskEntity event = disruptor.getRingBuffer().get(sequence);
            event.copyFrom(task);
        } finally {
            disruptor.getRingBuffer().publish(sequence);
        }
    }


    private void process(GenerateTaskEntity event) {
        log.info("Processing task: {}", JSONObject.toJSON(event));
        // Process the event
    }

}
