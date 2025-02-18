package com.alinesno.infra.base.search.memory.bean;

import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MemoryNode类代表一个记忆节点，用于存储用户相关的记忆信息
 * 它包含了记忆的元数据、内容、向量表示以及记忆的状态等信息
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class MemoryNode {

    // 时间戳生成器，用于为每个记忆节点分配唯一的时间戳
    private static final AtomicLong timestampGenerator = new AtomicLong(Instant.now().getEpochSecond());

    // 记忆的唯一标识符，使用UUID生成
    private String memoryId = IdUtil.getSnowflakeNextIdStr() ;

    // 用户名，表示记忆所属的用户
    private long agentId ;

    // 用户名，表示记忆所属的用户
    private String agentName;

    // 目标Id，表示记忆的目标或对象
    private String targetId ;

    // 目标名称，表示记忆的目标或对象
    private String targetName;

    // 元数据，用于存储记忆的相关信息，如时间、地点等
    private Map<String, String> metaData = new HashMap<>();

    // 记忆的内容
    private String content;

    // 记忆的键(有多个key存在)
    private String keys;

    // 记忆的向量表示，用于向量搜索和相似度计算
    private List<Double> keysVector = new ArrayList<>();

    // 记忆的值
    private String value;

    // 记忆的召回分数
    private double scoreRecall;

    // 记忆的排序分数
    private double scoreRank;

    // 记忆的重排序分数
    private double scoreRerank;

    // 记忆的类型
    private String memoryType;

    // 动作状态，表示记忆的当前动作状态，默认为"none"  new/content_modified/modified/deleted/none
    private String actionStatus = "none";

    // 存储状态，表示记忆的存储状态，默认为"valid"
    private String storeStatus = "valid";

    // 记忆的向量表示，可能与keyVector有区别，具体视业务逻辑而定
    private List<Double> contentVector = new ArrayList<>();

    // 记忆的时间戳，使用原子长整型生成，确保每个记忆节点的时间戳唯一
    private long timestamp = timestampGenerator.getAndIncrement();

    // 记忆的日期时间字符串，格式为"yyyy-MM-dd"
    private String dt;

    // 观察到的反射次数，用于记录记忆被反射的次数
    private int obsReflected;

    // 观察到的更新次数，用于记录记忆被更新的次数
    private int obsUpdated;

}