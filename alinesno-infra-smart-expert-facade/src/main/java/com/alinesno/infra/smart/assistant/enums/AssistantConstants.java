package com.alinesno.infra.smart.assistant.enums;

/**
 * 智能助手常量类
 */
public interface AssistantConstants {

    // 默认脚本处理
    String PREFIX_ASSISTANT_DEFAULT = "Agent_DefaultExpert" ;

    // groovy脚本流程引擎
    String PREFIX_ASSISTANT_SCRIPT = "Agent_ScriptExpert" ;

    // 知识库引擎
    String PREFIX_ASSISTANT_RAG = "Agent_RagExpert" ;

    // 流程脚本引擎
    String PREFIX_ASSISTANT_FLOW = "Agent_FlowExpert" ;

    // 流程脚本引擎
    String PREFIX_ASSISTANT_REACT = "Agent_ReActExpert" ;

    // 简单角色设定Prompt
    String PREFIX_ASSISTANT_SIMPLE = "Agent_SimpleExpert" ;

    // 深度搜索引擎
    String PREFIX_ASSISTANT_DEEP_SEARCH = "Agent_DeepSearchExpert" ;

}
