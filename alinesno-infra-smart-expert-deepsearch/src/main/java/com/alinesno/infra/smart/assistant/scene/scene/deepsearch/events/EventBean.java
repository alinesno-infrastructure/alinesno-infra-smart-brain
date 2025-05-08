package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events;

import lombok.Data;

import java.io.Serializable;

// 定义事件携带的数据对象
@Data
public class EventBean implements Serializable {

    private Long id;
    private String name;

    public EventBean(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}