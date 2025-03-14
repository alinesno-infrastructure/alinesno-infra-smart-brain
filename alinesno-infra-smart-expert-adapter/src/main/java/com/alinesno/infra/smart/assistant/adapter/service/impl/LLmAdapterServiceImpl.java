package com.alinesno.infra.smart.assistant.adapter.service.impl;

import com.agentsflex.core.image.ImageModel;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.image.gitee.GiteeImageModelConfig;
import com.agentsflex.llm.deepseek.DeepseekLlm;
import com.agentsflex.llm.deepseek.DeepseekLlmConfig;
import com.agentsflex.llm.doubao.DoubaoLlm;
import com.agentsflex.llm.doubao.DoubaoLlmConfig;
import com.agentsflex.llm.gitee.GiteeAiLlm;
import com.agentsflex.llm.gitee.GiteeAiLlmConfig;
import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.agentsflex.llm.openai.OpenAiLlm;
import com.agentsflex.llm.openai.OpenAiLlmConfig;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import com.agentsflex.llm.qwq.QwqLlm;
import com.agentsflex.llm.qwq.QwqLlmConfig;
import com.alinesno.infra.smart.assistant.enums.LlmModelProviderEnums;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

/**
 * LLmAdapterImpl
 */
@Component
public class LLmAdapterServiceImpl implements ILLmAdapterService {

    @Override
    public Llm getLlm(String type, LlmConfig config) {
        if (type == null || config == null) {
            throw new IllegalArgumentException("Type and config cannot be null");
        }

        // 工厂方法映射
        Map<String, Function<LlmConfig, Llm>> llmFactoryMap = Map.of(
                LlmModelProviderEnums.QWEN.getCode() , qwenConfig -> createLlm(new QwenLlmConfig(), qwenConfig, QwenLlm::new),
                LlmModelProviderEnums.OPENAI.getCode(), openAiConfig -> createLlm(new OpenAiLlmConfig(), openAiConfig, OpenAiLlm::new),
                LlmModelProviderEnums.DOUBAO.getCode(), doubaoAiConfig -> createLlm(new DoubaoLlmConfig(), doubaoAiConfig, DoubaoLlm::new),
                LlmModelProviderEnums.GITEE.getCode(), giteeAiConfig -> createLlm(new GiteeAiLlmConfig(), giteeAiConfig, GiteeAiLlm::new),
                LlmModelProviderEnums.OLLAMA.getCode(), ollamaAiConfig -> createLlm(new OllamaLlmConfig(), ollamaAiConfig, OllamaLlm::new),
                LlmModelProviderEnums.DEEPSEEK.getCode(), deepseekAiConfig -> createLlm(new DeepseekLlmConfig(), deepseekAiConfig, DeepseekLlm::new),
                LlmModelProviderEnums.QWQ.getCode(), QwqConfig -> createLlm(new QwqLlmConfig(), QwqConfig, QwqLlm::new)
        );

        // 获取对应的工厂方法
        Function<LlmConfig, Llm> factoryMethod = llmFactoryMap.get(type.toLowerCase());
        if (factoryMethod == null) {
            throw new UnsupportedOperationException("Unsupported LLM type: " + type);
        }

        return factoryMethod.apply(config);
    }

    // 提取公共逻辑到辅助方法
    private <T extends LlmConfig> Llm createLlm(T specificConfig, LlmConfig baseConfig, Function<T, Llm> llmConstructor) {
        specificConfig.setEndpoint(baseConfig.getEndpoint());
        specificConfig.setApiKey(baseConfig.getApiKey());
        specificConfig.setModel(baseConfig.getModel());
        return llmConstructor.apply(specificConfig);
    }


    @Override
    public ImageModel getImageModel(String type, GiteeImageModelConfig config) {
        return null;
    }
}
