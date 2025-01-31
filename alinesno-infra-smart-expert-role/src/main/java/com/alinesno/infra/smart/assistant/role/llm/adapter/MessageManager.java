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
    private long traceBusId ; // 业务跟踪的ID

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

    /**
     * 新增方法：将消息添加到列表的第一个位置，不考虑列表大小的限制。
     * @param msg 要添加的消息对象。
     */
    public void addFirst(Message msg) {
        // 直接在列表开头添加新元素
        list.add(0, msg);
    }

    public List<Message> get() {
        return list;
    }
}
