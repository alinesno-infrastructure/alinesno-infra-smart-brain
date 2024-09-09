package com.alinesno.infra.smart.inference.agent.model.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

// 反思的实体类
@NoArgsConstructor
@Data
public class Reflection {

    private String content; // 反思的内容
    private int experienceGain; // 根据反思获得的经验值增量
    private long timestamp; // 反思的时间戳

    public Reflection(String content, int experienceGain) {
        this.content = content;
        this.experienceGain = experienceGain;
        this.timestamp = System.currentTimeMillis();
    }
}