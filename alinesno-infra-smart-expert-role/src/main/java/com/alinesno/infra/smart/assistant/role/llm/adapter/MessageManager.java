package com.alinesno.infra.smart.assistant.role.llm.adapter;

import com.alibaba.dashscope.common.Message;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageManager {

    private final List<Message> list = new ArrayList<>();

    private final int messageCount ;
    private long channelId ; // 频道
    private long workflowId ;

    public MessageManager(int messageCount) {
        this.messageCount = messageCount;
    }

    public void add(Message msg) {

        // 如果超过messageCount，则之前的消息自动删除
        list.add(msg) ;
        while (list.size() >= messageCount) {
            list.remove(0);
        }

    }

    public List<Message> get() {
        return list;
    }
}
