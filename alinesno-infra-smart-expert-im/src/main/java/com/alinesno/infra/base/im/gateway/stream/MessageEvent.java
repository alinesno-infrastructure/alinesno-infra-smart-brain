package com.alinesno.infra.base.im.gateway.stream;

import com.alinesno.infra.base.im.gateway.provider.SSEChannelTaskController;
import com.alinesno.infra.base.im.gateway.utils.SSEUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

/**
 * 事件响应处理
 */
@Slf4j
@Component
public class MessageEvent {

    @Autowired
    private SSEUtils sseUtils;

    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * 处理任务消息
     * @param entries
     */
    @SneakyThrows
    public void handleTaskMessage(MapRecord<String, String, String> entries) {
        boolean isSend =  sseUtils.sendSseMessage(SSEChannelTaskController.CHANNEL_TASK_PRE , "123456789", entries.getValue().get("payload"));
        log.debug("is Send message = {}" , isSend);
    }

    /**
     * 处理完成结果消息
     * @param entries
     */
    @SneakyThrows
    public void handleResultMessage(MapRecord<String, String, String> entries) {

        boolean isSend = sseUtils.sendSseMessage(SSEChannelTaskController.CHANNEL_MESSAGE_PRE,
                "12345678",
                entries.getValue().get("payload"));

        log.debug("channelId() = {} ， is Send message = {}" , SSEChannelTaskController.CHANNEL_MESSAGE_PRE , isSend);

    }

}
