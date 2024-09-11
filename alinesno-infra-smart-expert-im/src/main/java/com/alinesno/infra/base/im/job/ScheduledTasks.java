package com.alinesno.infra.base.im.job;

import com.alinesno.infra.base.im.event.SSEMessageEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    private SSEMessageEventService eventService;

    // 每隔 5 秒执行一次
//    @Scheduled(fixedRate = 60*1000)
//    public void reportCurrentTime() {
//        log.info("现在的时间是: {}", LocalDateTime.now());
//        eventService.triggerCustomEvent(AgentUtils.genSSEChatMessageDto());
//    }

}