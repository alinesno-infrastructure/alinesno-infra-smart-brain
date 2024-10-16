package com.alinesno.infra.smart.assistant.api.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

/**
 * 该类用于描述流程定义的相关属性和状态，包括流程的唯一标识，名称，类型，状态以及额外的属性和参数
 */
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessTaskDto {

    // 流程数据库标识
    private long taskId;

    // 流程定义的唯一标识
    private String id;

    // 流程定义的名称
    private String name;

    // 流程定义的描述
    private String description;

    // 流程定义的类型
    private int type;

    // 流程定义的状态
    private int status;

    // 流程定义的额外属性
    private Object attr;

    // 流程定义的参数
    private ParamsDto params;

}
