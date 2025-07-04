package com.alinesno.infra.smart.assistant.enums;

public interface AssistantConstants {

    String PREFIX_ASSISTANT = "Agent_" ;

    // 默认脚本处理
    String PREFIX_ASSISTANT_DEFAULT = "Agent_DefaultExpert" ;

    // groovy脚本流程引擎
    String PREFIX_ASSISTANT_SCRIPT = "Agent_ScriptExpert" ;

    // 流程脚本引擎
    String PREFIX_ASSISTANT_FLOW = "Agent_FlowExpert" ;

    // 流程脚本引擎
    String PREFIX_ASSISTANT_REACT = "Agent_ReActExpert" ;

    // 携带外部知识库
    String PREFIX_ASSISTANT_REACT_OUTSIDE = "Agent_ReActOutsideExpert" ;

    // 简单角色设定Prompt
    String PREFIX_ASSISTANT_SIMPLE = "Agent_SimpleExpert" ;

    // 深度搜索引擎
    String PREFIX_ASSISTANT_DEEP_SEARCH = "Agent_DeepSearchExpert" ;

}
