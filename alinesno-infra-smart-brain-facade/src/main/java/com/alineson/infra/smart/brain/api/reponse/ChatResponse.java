package com.alineson.infra.smart.brain.api.reponse;

/**
 * 聊天响应
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public class ChatResponse {

    /**
     * 状态
     */
    private String status;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private Object data;

    /**
     * 父消息ID
     */
    private String parentMessageId;

    public String getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(String parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 成功
     *
     * @return 聊天响应
     */
    public static ChatResponse success() {
        ChatResponse response = new ChatResponse();
        response.setStatus("Success");
        return response;
    }

    /**
     * 成功
     *
     * @param data 数据
     * @return 聊天响应
     */
    public static ChatResponse success(Object data) {
        ChatResponse response = success();
        response.setData(data);
        return response;
    }

    /**
     * 成功
     *
     * @param msg 消息
     * @return 聊天响应
     */
    public static ChatResponse successMsg(String msg) {
        ChatResponse response = success();
        response.setMessage(msg);
        return response;
    }

}