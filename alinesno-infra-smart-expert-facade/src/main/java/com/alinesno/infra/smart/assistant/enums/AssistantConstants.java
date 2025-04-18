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

//    // 帮助角色引擎
//    String AIP_HELP_AGENT =  "AIP_DEFAULT_HELP_AGENT";

    // 简单角色设定Prompt
    String PREFIX_ASSISTANT_SIMPLE = "Agent_SimpleExpert" ;

}
