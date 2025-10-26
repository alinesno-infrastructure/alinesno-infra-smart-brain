package com.alinesno.infra.smart.assistant.api.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 深度搜索提示数据
 *
 * 该类用于封装深度搜索功能中使用的各类提示信息，
 * 包括计划提示、工作提示和摘要提示，通过Builder模式构建对象实例。
 */
@Data
@NoArgsConstructor
public class DeepSearchPromptData {

    /**
     * 计划提示信息
     * 用于指导搜索计划生成的提示内容
     */
    private String planPrompt ;

    /**
     * 工作提示信息
     * 用于指导具体工作执行的提示内容
     */
    private String workerPrompt ;

    /**
     * 摘要提示信息
     * 用于指导摘要生成的提示内容
     */
    private String summaryPrompt ;

}

