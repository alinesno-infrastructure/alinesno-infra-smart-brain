package com.alineson.infra.base.notice.third.service;

import com.alinesno.infra.base.notice.api.NoticeRequestDto;
import com.alinesno.infra.base.notice.api.NoticeResponseDto;

public interface SmsService {

    public NoticeResponseDto sendSms(NoticeRequestDto requestDto) ;

}
