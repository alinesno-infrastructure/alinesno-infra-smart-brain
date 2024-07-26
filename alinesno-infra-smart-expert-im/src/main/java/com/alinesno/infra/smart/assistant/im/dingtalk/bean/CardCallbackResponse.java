package com.alinesno.infra.smart.assistant.im.dingtalk.bean;

import lombok.Data;

import java.util.Map;

/**
 * 卡片回调响应
 */
@Data
public class CardCallbackResponse {

    //卡片公有数据
    private CardDataDTO cardData;

    //触发回调用户的私有数据
    private CardDataDTO privateCardData;

    public static class CardDataDTO{

        //卡片参数
        private Map<String, String> cardParamMap;
    }
}