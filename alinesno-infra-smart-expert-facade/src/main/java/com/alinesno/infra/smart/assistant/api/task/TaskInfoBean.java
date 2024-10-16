package com.alinesno.infra.smart.assistant.api.task;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 任务信息
 */
@ToString
@Data
public class TaskInfoBean implements Serializable {

    // 运行的工作空间
    private String workspace ;

//    // 环境定义
//    private EnvironmentEntity environment ;
//
//    // 任务定义
//    private ProcessDefinitionEntity process ;
//
//    // 任务列表
//    private TaskDefinitionEntity task;
//
//    // 资源列表
//    private List<ResourceEntity> resources;

}
