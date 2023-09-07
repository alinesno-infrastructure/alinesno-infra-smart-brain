package com.alinesno.infra.smart.brain.service;

import com.alinesno.infra.smart.brain.api.NoticeRequestDto;
import com.alinesno.infra.smart.brain.api.NoticeResponseDto;

public interface SmsService {

    public NoticeResponseDto sendSms(NoticeRequestDto requestDto) ;

}
