package com.alinesno.infra.base.search.memory.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MemoryNodeWithScore类是一个用于封装内存节点和相似度的类。
 */
@NoArgsConstructor
@Data
public class MemoryNodeWithScore {

    private String targetId ;
    private String targetName ;

    private String content ;
    private String keys;
    private double similarity;

    public MemoryNodeWithScore(String content, double similarity) {
        this.content = content;
        this.similarity = similarity;
    }

}
