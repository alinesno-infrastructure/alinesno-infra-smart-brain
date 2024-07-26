package com.alinesno.infra.smart.assistant.im.dingtalk.service;

import com.alinesno.infra.smart.assistant.im.dingtalk.dto.DingtalkRobotMessageDto;
import com.alinesno.infra.smart.assistant.im.dingtalk.event.DingtalkMsgDispatcher;
import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;
import com.alinesno.infra.smart.assistant.im.service.IDingtalkNoticeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 钉钉执行机器人
 */
@Slf4j
@Service
public class DingtalkNoticeServiceImpl implements IDingtalkNoticeService {

    @Autowired
    private DingtalkMsgDispatcher dingtalkMsgDispatcher ;

    /**
     * 发送执行结果通知给机器人
     * @param noticeDto
     */
    @SneakyThrows
    @Override
    public void noticeAgent(NoticeDto noticeDto) {

        DingtalkRobotMessageDto dto = new DingtalkRobotMessageDto() ;

        dto.setAtAll(false);
        dto.setAtUser(noticeDto.getSender());
        dto.setMessageType("markdown");
        dto.setMessageContent(noticeDto.getTaskName());

        dingtalkMsgDispatcher.sendMessageWebhook(dto , noticeDto) ;
    }

}
