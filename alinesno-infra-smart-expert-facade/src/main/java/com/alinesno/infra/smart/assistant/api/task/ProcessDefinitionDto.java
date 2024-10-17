package com.alinesno.infra.smart.assistant.api.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 流程定义信息
 * 该类用于封装流程定义的相关信息，包括流程的上下文环境和任务列表
 */
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessDefinitionDto implements Serializable {

    /**
     * 项目ID
     */
    private long projectId ;

    /**
     * 角色ID
     */
    private long roleId ;

    /**
     * 流程上下文信息
     * 包含了流程执行时的环境和配置信息
     */
//    private ProcessContextDto context ;

    /**
     * 流程任务列表
     * 定义了流程中包含的所有任务信息
     */
    private List<ProcessTaskDto> taskFlow ;

}
