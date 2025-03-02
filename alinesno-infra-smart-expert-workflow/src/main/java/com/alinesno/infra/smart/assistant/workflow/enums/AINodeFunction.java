//package com.alinesno.infra.smart.assistant.workflow.enums;
//
//import lombok.Getter;
//
///**
// * AINodeFunction
// */
//@Getter
//public enum AINodeFunction {
//
//    AI_CONVERSATION("AI_CONVERSATION", "AI 对话", "与 AI 大模型进行对话"),
//    IMAGE_UNDERSTANDING("IMAGE_UNDERSTANDING", "图片理解", "识别出图片中的对象、场景等信息回答用户问题"),
//    IMAGE_GENERATION("IMAGE_GENERATION", "图片生成", "根据提供的文本内容生成图片"),
//    KNOWLEDGE_BASE_RETRIEVAL("KNOWLEDGE_BASE_RETRIEVAL", "知识库检索", "关联知识库，查找与问题相关的分段"),
//    MULTIWAY_RECALL("MULTIWAY_RECALL", "多路召回", "使用重排模型对多个知识库的检索结果进行二次召回"),
//    CONDITION("CONDITION", "判断器", "根据不同条件执行不同的节点"),
//    SPECIFIED_REPLY("SPECIFIED_REPLY", "指定回复", "指定回复内容，引用变量会转换为字符串进行输出"),
//    FORM_COLLECTION("FORM_COLLECTION", "表单收集", "在问答过程中用于收集用户信息，可以根据收集到表单数据执行后续流程"),
//    QUESTION_OPTIMIZATION("QUESTION_OPTIMIZATION", "问题优化", "根据历史聊天记录优化完善当前问题，更利于匹配知识库分段"),
//    DOCUMENT_CONTENT_EXTRACTION("DOCUMENT_CONTENT_EXTRACTION", "文档内容提取", "提取文档中的内容"),
//    SPEECH_TO_TEXT("SPEECH_TO_TEXT", "语音转文本", "将音频通过语音识别模型转换为文本"),
//    TEXT_TO_SPEECH("TEXT_TO_SPEECH", "文本转语音", "将文本通过语音合成模型转换为音频");
//
//    private final String code;
//    private final String name;
//    private final String desc;
//
//    AINodeFunction(String code, String name, String desc) {
//        this.code = code;
//        this.name = name;
//        this.desc = desc;
//    }
//
//}