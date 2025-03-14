package com.alinesno.infra.smart.assistant.adapter.service;

import com.agentsflex.core.image.ImageModel;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.image.gitee.GiteeImageModelConfig;

/**
 * LLmAdapter 大模型适配器
 */
public interface ILLmAdapterService {

    /**
     * 根据不同的类返回对应的Llm对象
     * @param type
     * @return
     */
    Llm getLlm(String type , LlmConfig config);

    /**
     * 根据不同的类返回对应的ImageModel对象
     * @param type
     * @param config
     * @return
     */
    ImageModel getImageModel(String type , GiteeImageModelConfig config);

}
