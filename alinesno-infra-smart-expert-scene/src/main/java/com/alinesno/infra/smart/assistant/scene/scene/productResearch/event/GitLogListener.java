package com.alinesno.infra.smart.assistant.scene.scene.productResearch.event;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.im.service.ISSEService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Git日志事件监听器
 */
@Slf4j
@Component
public class GitLogListener implements ApplicationListener<GitLogEvent> {

     @Autowired
    private ISSEService sseService;

    private String channelStreamId ;

    @Override
    public void onApplicationEvent(GitLogEvent event) {
        channelStreamId = event.getChannelStreamId() ;
        handleLog(event.getLogLevel(), event.getLogMessage(), event.getSourceClass() , event.getLogTime());
    }

    @SneakyThrows
    private void handleLog(String level, String message, String sourceClass , String logTime) {

        // 示例处理逻辑：根据日志级别打印到不同地方
        JSONObject json = new JSONObject();

        json.put("level", level);
        json.put("message", message);
        json.put("sourceClass", sourceClass);
        json.put("logTime", logTime);

        try{
            if(!level.equals("END")){
                sseService.sendNotDone(channelStreamId , json.toJSONString());
            } else {
                sseService.sendDone(channelStreamId);
            }
        }catch (Exception e){
            log.error("发送消息失败", e);
        }

    }
}