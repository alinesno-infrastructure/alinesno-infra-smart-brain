package com.alinesno.infra.smart.assistant.im.dingtalk.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 卡片回调请求
 */
@Data
public class CardCallbackRequest{

    /**
     * 回调类型,actionCallback
     */
    private String type;

    /**
     * 发起事件回调卡片的ID
     */
    private String outTrackId;

    /**
     * 回调内容,ActionCallbackContent的jsonString格式
     */
    private String content;

    /**
     * 卡片归属的企业id
     */
    private String corpId;

    /**
     * 用户userId
     */
    private String userId;

    /**
     * 回调按钮的内容信息
     */
    public static class ActionCallbackContent {
        private PrivateCardActionData cardPrivateData;

        public static class PrivateCardActionData {
            //点击按钮的id
            private List<String> actionIds;

            //给按钮配置的额外参数
            private Map<String, Object> params;

        }
    }
}

