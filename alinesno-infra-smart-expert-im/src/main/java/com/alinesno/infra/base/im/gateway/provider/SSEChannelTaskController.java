package com.alinesno.infra.base.im.gateway.provider;

import com.alinesno.infra.base.im.gateway.utils.SSEUtils;
import com.alinesno.infra.common.facade.response.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * 频道SSE消息推送，主要用于任务及任务状态的推送
 */
@RestController
@Slf4j
@CrossOrigin
@RequestMapping(value = "/v1/api/infra/base/im/sseChannelTask")
public class SSEChannelTaskController {

    public static final String CHANNEL_TASK_PRE = "task_9527" ;
    public static final String CHANNEL_MESSAGE_PRE = "message_9527" ;

    @Autowired
    private SSEUtils sseUtils;

    @GetMapping(value = "/createSseConnect", produces="text/event-stream;charset=UTF-8")
    public SseEmitter createSseConnect(String type) {
        log.debug("连接频道:{}" , type);
        return sseUtils.connect(getPre(type));
    }

    @PostMapping("/sendSseMessage")
    public void sendSseMessage(@RequestParam("message")  String message , String type){
        boolean isSend =  sseUtils.sendSseMessage(getPre(type), "123456789", message);
        log.debug("is Send message = {}" , isSend);
    }
 
    @GetMapping(value = "/listSseConnect")
    public AjaxResult listSseConnect(){
        Map<String, SseEmitter> sseEmitterMap = sseUtils.listSseConnect();
        return AjaxResult.success(sseEmitterMap);
    }
 
 
    /**
     * 关闭SSE连接
     *
     **/
    @GetMapping("/closeSseConnect")
    public AjaxResult closeSseConnect(String type) {

        log.debug("closeSseConnect = {}" , type);

        sseUtils.deleteChannel(getPre(type));
        return AjaxResult.success();
    }

    /**
     * 获取到类型
     * @param type
     * @return
     */
    private String getPre(String type) {
        if("task".equals(type)){
            return CHANNEL_TASK_PRE ;
        } else if("message".equals(type)){
            return CHANNEL_MESSAGE_PRE ;
        }
        return "" ;
    }

}