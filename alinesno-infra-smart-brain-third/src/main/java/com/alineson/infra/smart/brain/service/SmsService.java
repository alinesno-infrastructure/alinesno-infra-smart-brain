package com.alineson.infra.smart.brain.service;

import com.alineson.infra.smart.brain.api.NoticeRequestDto;
import com.alineson.infra.smart.brain.api.NoticeResponseDto;

public interface SmsService {

    public NoticeResponseDto sendSms(NoticeRequestDto requestDto) ;

}
