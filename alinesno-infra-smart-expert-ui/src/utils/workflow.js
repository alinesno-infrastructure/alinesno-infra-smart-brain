const WorkflowType = {
    Base: 'base',
    Start: 'start',
    End: 'end',
    AiChat: 'ai_chat',
    SearchDataset: 'knowledge_search',
    Question: 'question',
    Condition: 'condition',
    Reply: 'reply',
    HttpApi: 'http_api',
    FunctionLib: 'function_lib',
    FunctionLibCustom: 'function',
    RrerankerNode: 'reranker',
    Application: 'application',
    DocumentExtractNode: 'document_extract',
    ImageUnderstandNode: 'image_understand',
    FormNode: 'form',
    TextToSpeechNode: 'text_to_speech',
    SpeechToTextNode: 'speech_to_text',
    ImageGenerateNode: 'image_generate'
};

export { WorkflowType };