package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean;

import lombok.Data;

/**
 * 任务实体类
 */
@Data
public class DeepTaskBean {

    /**
     * 任务的唯一标识符
     */
    private String id ;

    /**
     * 任务的目标或目的
     */
    private String taskName;

    /**
     * 任务的详细描述
     */
    private String taskDesc;

    /**
     * 任务的优先级或顺序
     */
    private int order ;

    /**
     * 任务执行的上下文或环境信息
     */
    private String context ;

    /**
     * 可用于任务执行的工具或资源信息
     */
    private String tools ;

}
