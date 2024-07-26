package com.alinesno.infra.smart.assistant.im.service;

import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;

/**
 * 钉钉完成机器人
 */
public interface IDingtalkNoticeService {

    /**
     * 执行完成通知机器人
     * @param noticeDto
     */
    public void noticeAgent(NoticeDto noticeDto) ;

}
