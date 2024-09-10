package com.alinesno.infra.base.im.job;

import com.alinesno.infra.base.im.event.SSEMessageEventService;
import com.alinesno.infra.base.im.utils.AgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    private SSEMessageEventService eventService;

    // 每隔 5 秒执行一次
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("现在的时间是: {}", LocalDateTime.now());
        eventService.triggerCustomEvent(AgentUtils.genSSEChatMessageDto());
    }
}