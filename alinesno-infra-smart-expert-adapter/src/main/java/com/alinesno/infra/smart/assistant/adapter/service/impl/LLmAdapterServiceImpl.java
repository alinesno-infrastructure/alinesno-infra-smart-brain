package com.alinesno.infra.smart.assistant.adapter.service.impl;

import com.agentflex.ocr.aip.AipOcrModel;
import com.agentflex.ocr.aip.AipOcrModelConfig;
import com.agentflex.vision.qwen.QwenVision;
import com.agentflex.vision.qwen.QwenVisionConfig;
import com.agentsflex.core.image.ImageConfig;
import com.agentsflex.core.image.ImageModel;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.ocr.OcrConfig;
import com.agentsflex.core.ocr.OcrModel;
import com.agentsflex.core.reranker.ReRanker;
import com.agentsflex.core.reranker.ReRankerConfig;
import com.agentsflex.core.speech.SpeechConfig;
import com.agentsflex.core.speech.SpeechModel;
import com.agentsflex.image.doubao.DoubaoImageModel;
import com.agentsflex.image.doubao.DoubaoImageModelConfig;
import com.agentsflex.image.gitee.GiteeImageModel;
import com.agentsflex.image.gitee.GiteeImageModelConfig;
import com.agentsflex.image.qwen.QwenImageModel;
import com.agentsflex.image.qwen.QwenImageModelConfig;
import com.agentsflex.image.siliconflow.SiliconImageModel;
import com.agentsflex.image.siliconflow.SiliconflowImageModelConfig;
import com.agentsflex.llm.deepseek.DeepseekLlm;
import com.agentsflex.llm.deepseek.DeepseekLlmConfig;
import com.agentsflex.llm.doubao.DoubaoLlm;
import com.agentsflex.llm.doubao.DoubaoLlmConfig;
import com.agentsflex.llm.jdcloud.JDcloudLlmConfig;
import com.agentsflex.llm.jdcloud.JDcloudLlm;
import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import com.agentsflex.llm.qwq.QwqLlm;
import com.agentsflex.llm.qwq.QwqLlmConfig;
import com.agentsflex.llm.siliconflow.SiliconflowLlm;
import com.agentsflex.llm.siliconflow.SiliconflowLlmConfig;
import com.agentsflex.reranker.qwen.QwenReRanker;
import com.agentsflex.reranker.qwen.QwenRerankerConfig;
import com.agentsflex.reranker.siliconflow.SiliconFlowReRanker;
import com.agentsflex.reranker.siliconflow.SiliconFlowRerankerConfig;
import com.agentsflex.speech.qwen.QwenSpeechModel;
import com.agentsflex.speech.qwen.QwenSpeechModelConfig;
import com.agentsflex.speech.siliconflow.SiliconflowSpeechModel;
import com.agentsflex.speech.siliconflow.SiliconflowSpeechModelConfig;
import com.alinesno.infra.smart.assistant.adapter.enums.LlmModelProviderEnums;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

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
                LlmModelProviderEnums.QWEN.getCode(), qwenConfig -> createLlm(new QwenLlmConfig(), qwenConfig, QwenLlm::new),
                LlmModelProviderEnums.DOUBAO.getCode(), doubaoAiConfig -> createLlm(new DoubaoLlmConfig(), doubaoAiConfig, DoubaoLlm::new),
                LlmModelProviderEnums.DEEPSEEK.getCode(), deepseekAiConfig -> createLlm(new DeepseekLlmConfig(), deepseekAiConfig, DeepseekLlm::new),
                LlmModelProviderEnums.OLLAMA.getCode(), ollamaAiConfig -> createLlm(new OllamaLlmConfig(), ollamaAiConfig, OllamaLlm::new),
                LlmModelProviderEnums.SILICONFLOW.getCode(), siliconAiConfig -> createLlm(new SiliconflowLlmConfig(), siliconAiConfig, SiliconflowLlm::new),
                LlmModelProviderEnums.QWQ.getCode(), QwqConfig -> createLlm(new QwqLlmConfig(), QwqConfig, QwqLlm::new),
                LlmModelProviderEnums.JDCloud.getCode(), jdcloudConfig -> createLlm(new JDcloudLlmConfig(), jdcloudConfig, JDcloudLlm::new)
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
        specificConfig.setApiSecret(baseConfig.getApiSecret());

        specificConfig.setModel(baseConfig.getModel());
        return llmConstructor.apply(specificConfig);
    }


    @Override
    public ImageModel getImageModel(String type, ImageConfig config) {

        // 参数校验
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }

        // 配置字段校验
        if (config.getEndpoint() == null || config.getModel() == null) {
            throw new IllegalArgumentException("Config fields 'endpoint' and 'model' cannot be null");
        }

        // 工厂模式实现
        Supplier<ImageModel> supplier = getImageModelSupplier(type, config);
        if (supplier != null) {
            return supplier.get();
        } else {
            // 默认处理逻辑
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    @Override
    public ReRanker reranker(String type , ReRankerConfig config) {

        // siliconflow和qwen的重排序
        if (LlmModelProviderEnums.SILICONFLOW.getCode().equals(type)) {

            SiliconFlowRerankerConfig rerankerConfig = new SiliconFlowRerankerConfig();
            rerankerConfig.setApiKey(config.getApiKey()) ;
            rerankerConfig.setEndpoint(config.getEndpoint());
            rerankerConfig.setModel(config.getModel()) ;

            return new SiliconFlowReRanker(rerankerConfig);
        } else if (LlmModelProviderEnums.QWEN.getCode().equals(type)) {

            QwenRerankerConfig rerankerConfig = new QwenRerankerConfig();
            rerankerConfig.setApiKey(config.getApiKey()) ;
            rerankerConfig.setEndpoint(config.getEndpoint());
            rerankerConfig.setModel(config.getModel()) ;

            return new QwenReRanker(rerankerConfig);
        }

        return null;
    }

    @Override
    public SpeechModel speechModel(String type, SpeechConfig config) {
        if (LlmModelProviderEnums.QWEN.getCode().equals(type)) {

            QwenSpeechModelConfig speechModelConfig = new QwenSpeechModelConfig();
            speechModelConfig.setEndpoint(config.getEndpoint());
            speechModelConfig.setApiKey(config.getApiKey());
            speechModelConfig.setModel(config.getModel());

            return new QwenSpeechModel(speechModelConfig);
        } else if(LlmModelProviderEnums.SILICONFLOW.getCode().equals(type)){

            SiliconflowSpeechModelConfig speechModelConfig = new SiliconflowSpeechModelConfig();
            speechModelConfig.setEndpoint(config.getEndpoint());
            speechModelConfig.setApiKey(config.getApiKey());
            speechModelConfig.setModel(config.getModel());

            return new SiliconflowSpeechModel(speechModelConfig);
        }
        return null;
    }

    @Override
    public OcrModel ocrModel(String type, OcrConfig config) {
        if(LlmModelProviderEnums.AIP.getCode().equals(type)){
            AipOcrModelConfig aipConfig = new AipOcrModelConfig();
            return new AipOcrModel(aipConfig);
        }
        return null;
    }

    @Override
    public Llm visionModel(String type, LlmConfig config) {
        if(LlmModelProviderEnums.QWEN.getCode().equals(type)){
            QwenVisionConfig visionConfig = new QwenVisionConfig();

            visionConfig.setEndpoint(config.getEndpoint());
            visionConfig.setApiKey(config.getApiKey()) ;
            visionConfig.setModel(config.getModel()) ;

            return new QwenVision(visionConfig);
        }
        return null;
    }

    private Supplier<ImageModel> getImageModelSupplier(String type, ImageConfig config) {
        Map<String, Supplier<ImageModel>> imageModelFactory = Map.of(
                LlmModelProviderEnums.DOUBAO.getCode(), () -> createDoubaoImageModel(config),
                LlmModelProviderEnums.QWEN.getCode(), () -> createQwenImageModel(config),
                LlmModelProviderEnums.SILICONFLOW.getCode(), () -> createSiliconflowImageModel(config)
        );

        // 获取对应类型的 ImageModel
        return imageModelFactory.get(type);
    }

    // 工厂方法：创建 GiteeImageModel
    private ImageModel createGiteeImageModel(ImageConfig config) {
        GiteeImageModelConfig imageModelConfig = new GiteeImageModelConfig();
        imageModelConfig.setEndpoint(config.getEndpoint());
        imageModelConfig.setApiKey(config.getApiKey());
        imageModelConfig.setModel(config.getModel());
        return new GiteeImageModel(imageModelConfig);
    }

    // 工厂方法：创建 DoubaoImageModel
    private ImageModel createDoubaoImageModel(ImageConfig config) {
        DoubaoImageModelConfig imageModelConfig = new DoubaoImageModelConfig();
        imageModelConfig.setEndpoint(config.getEndpoint());
        imageModelConfig.setAccessKey(config.getApiKey());
        imageModelConfig.setSecretKey(config.getSecretKey());
        imageModelConfig.setModel(config.getModel());
        return new DoubaoImageModel(imageModelConfig);
    }

    // 工厂方法：创建 QwenImageModel
    private ImageModel createQwenImageModel(ImageConfig config) {
        QwenImageModelConfig imageModelConfig = new QwenImageModelConfig();
        imageModelConfig.setEndpoint(config.getEndpoint());
        imageModelConfig.setApiKey(config.getApiKey());
        imageModelConfig.setModel(config.getModel());
        return new QwenImageModel(imageModelConfig);
    }

    // 工厂方法：创建 SiliconflowImageModel
    private ImageModel createSiliconflowImageModel(ImageConfig config) {
        SiliconflowImageModelConfig imageModelConfig = new SiliconflowImageModelConfig();
        imageModelConfig.setEndpoint(config.getEndpoint());
        imageModelConfig.setApiKey(config.getApiKey());
        imageModelConfig.setModel(config.getModel());
        return new SiliconImageModel(imageModelConfig);
    }


}
