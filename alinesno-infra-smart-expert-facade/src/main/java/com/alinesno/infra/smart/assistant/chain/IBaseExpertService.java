package com.alinesno.infra.smart.assistant.chain;

import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;

import java.util.Map;

// 创建 Expert 接口
public interface IBaseExpertService {

    /**
     * 专家运行
     *
     * @param params
     * @param chainName
     * @param chainId
     * @param noticeDto
     */
    public void processExpert(Map<String, Object> params, String chainName , Long chainId, NoticeDto noticeDto) ;

}