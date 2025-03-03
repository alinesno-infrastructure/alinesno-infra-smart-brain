package com.alinesno.infra.smart.assistant.workflow.nodes.variable;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 该类用于表示全局变量，封装了一些在程序运行过程中可能会用到的通用信息，
 * 如当前时间、上一个节点的内容、频道号以及历史对话内容等。
 */
@Data
public class GlobalVariables {
    /**
     * 当前时间，在对象创建时自动获取并格式化，格式为 "yyyy-MM-dd HH:mm:ss"，
     * 一旦对象创建完成，该时间不会再发生变化，因此使用 final 修饰。
     */
    private final String time;
    /**
     * 上一个节点的内容，可根据实际情况进行修改和获取。
     */
    private String preContent;
    /**
     * 频道号，用于标识不同的频道，可通过 setter 方法进行修改。
     */
    private Long channelId;
    /**
     * 历史对话内容，记录了之前的对话信息，同样可以动态修改。
     */
    private List<String> historyContent;

    /**
     * 构造函数，用于初始化 GlobalVariables 对象。
     * 在创建对象时，会自动获取当前时间并进行格式化，同时接收上节点内容、
     * 频道号和历史对话内容作为参数来初始化相应的字段。
     * 
     * @param preContent 上一个节点的内容
     * @param channelId  频道号
     * @param historyContent 历史对话内容
     */
    public GlobalVariables(String preContent, Long channelId, List<String> historyContent) {
        // 获取当前时间并格式化
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.time = now.format(formatter);
        this.preContent = preContent;
        this.channelId = channelId;
        this.historyContent = historyContent;
    }

}