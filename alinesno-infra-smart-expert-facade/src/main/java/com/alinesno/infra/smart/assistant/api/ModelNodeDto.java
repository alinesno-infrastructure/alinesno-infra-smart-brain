package com.alinesno.infra.smart.assistant.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Data
@Slf4j
public class ModelNodeDto {

    // 节点唯一标识符
    private String id;
    // 是否打印
    private boolean isPrint;
    // 是否最后一个节点
    private boolean isLastNode;
    // 节点类型，表示节点的种类，如开始节点、任务节点等
    private String type;
    // 节点名称
    private String stepName;

    public ModelNodeDto(String id, boolean isPrint) {
        this.id = id;
        this.isPrint = isPrint;
    }
}