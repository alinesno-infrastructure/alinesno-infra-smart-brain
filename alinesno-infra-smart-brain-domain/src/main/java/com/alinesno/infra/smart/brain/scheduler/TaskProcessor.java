package com.alinesno.infra.smart.brain.scheduler;

import com.alinesno.infra.smart.brain.entity.GenerateTaskEntity;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class TaskProcessor {

    @Autowired
    private IGenerateTaskService aiGenTaskService;

    @Value("${alinesno.infra.smart.brain.disruptor.bufferSize:1024}")
    private int bufferSize;

    private volatile int batchSize;

    private Disruptor<GenerateTaskEntity> disruptor;

    @PostConstruct
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

        loadTasks();
    }

    private void loadTasks() {
        List<GenerateTaskEntity> tasks = aiGenTaskService.getAllUnfinishedTasks();
        for (GenerateTaskEntity task : tasks) {
            long sequence = disruptor.getRingBuffer().next();
            try {
                GenerateTaskEntity event = disruptor.getRingBuffer().get(sequence);
                event.copyFrom(task);
            } finally {
                disruptor.getRingBuffer().publish(sequence);
            }
        }
    }

    private void process(GenerateTaskEntity event) {
        log.info("Processing task: {}", event.getBusinessId());
        // Process the event
    }

    public void consume() {
        while (true) {
            IntStream.range(0, batchSize)
                    .parallel()
                    .forEach(i -> {
                        GenerateTaskEntity task = disruptor.getRingBuffer().get(i);
                        process(task);
                    });
        }
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
