package com.alinesno.infra.smart.assistant.adapter.enums;

import com.agentflex.ocr.aip.AipOcrModelEnums;
import com.agentflex.vision.qwen.QwenVisionModelEnums;
import com.agentsflex.image.doubao.DoubaoImageModelEnums;
import com.agentsflex.image.qwen.QwenImageModelEnums;
import com.agentsflex.llm.deepseek.DeepSeekModelEnums;
import com.agentsflex.llm.deepseek.DeepseekLlm;
import com.agentsflex.llm.jdcloud.JDcloudLlmModelEnums;
import com.agentsflex.llm.ollama.OllamaLlmModelEnums;
import com.agentsflex.llm.qwen.QwenLlmModelEnums;
import com.agentsflex.llm.qwen.QwenVectorModelEnums;
import com.agentsflex.llm.qwq.QwQLlmModelEnums;
import com.agentsflex.llm.siliconflow.SiliconflowLlmModelEnums;
import com.agentsflex.llm.siliconflow.SiliconflowVectorModelEnums;
import com.agentsflex.reranker.qwen.QwenReRankerModelEnums;
import com.agentsflex.reranker.siliconflow.SiliconflowRerankerModels;
import com.agentsflex.speech.qwen.QwenSpeechModelEnums;
import com.agentsflex.speech.qwen.QwenSpeechRecognizeModelEnums;
import com.agentsflex.speech.siliconflow.SiliconflowSpeechModelEnums;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.enums.ModelTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Getter
public enum LlmModelProviderEnums {

    QWEN("阿里百炼(Qwen)", "qwen", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://dashscope.aliyuncs.com/compatible-mode/v1" , QwenLlmModelEnums.getAllModels()),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://dashscope.aliyuncs.com/compatible-mode/v1" , QwenVectorModelEnums.getAllModels()),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank/text-rerank" , QwenReRankerModelEnums.getAllModels()),
            new ModelInfo(ModelTypeEnums.SPEECH_RECOGNITION, "https://dashscope.aliyuncs.com/api/v1/services/audio/asr/transcription" , QwenSpeechRecognizeModelEnums.getAllModels()) ,
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "https://dashscope.aliyuncs.com/compatible-mode/v1" , QwenSpeechModelEnums.getAllModels() , 2) ,
            new ModelInfo(ModelTypeEnums.VISION_MODEL, "https://dashscope.aliyuncs.com/compatible-mode/v1" , QwenVisionModelEnums.getAllModels()) ,
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis" , QwenImageModelEnums.getAllModels())
    )),

    // 添加DeepSeek大模型
    DEEPSEEK("深度求索", "deepseek", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://api.deepseek.com" , DeepSeekModelEnums.getAllModels())
    )),

    DOUBAO("云雀大模型", "doubao", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://ark.cn-beijing.volces.com/api/v3"),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://ark.cn-beijing.volces.com/api/v3/vector"),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://ark.cn-beijing.volces.com/api/v3/ranking"),
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "https://visual.volcengineapi.com" , DoubaoImageModelEnums.getAllModels())
    )),

    GITEE("GiteeAI", "gitee", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://ai.gitee.com/v1"),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://ai.gitee.com/v1/vector"),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://ai.gitee.com/v1/ranking"),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "https://ai.gitee.com/v1/speech-synthesis"),
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "https://ai.gitee.com/v1/image-generation")
    )),

    OLLAMA("Ollama", "ollama", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "http://localhost:11434" , OllamaLlmModelEnums.getAllModels()),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "http://localhost:11434/speech-synthesis"),
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "http://localhost:11434/image-generation")
    )),

    SILICONFLOW("硅基流动", "siliconflow", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://api.siliconflow.cn/v1" , SiliconflowLlmModelEnums.getAllModels()),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://api.siliconflow.cn/v1" , SiliconflowVectorModelEnums.getAllModels()),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://api.siliconflow.cn/v1/ranking" , SiliconflowRerankerModels.getAllModels()),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "https://api.siliconflow.cn/v1/audio/speech" , SiliconflowSpeechModelEnums.getAllModels() , 2)
    )),

    QWQ("阿里百炼（QwQ）", "qwq", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://dashscope.aliyuncs.com/compatible-mode/v1" , QwQLlmModelEnums.getAllModels())
    )),

    JDCloud("京东灵犀", "jdcloud", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "http://jdcloud.linesno.com/v1" , JDcloudLlmModelEnums.getAllModels())
    )),

    AIP("AIP模型", "aip", List.of(
            new ModelInfo(ModelTypeEnums.OCR_MODEL, "http://alinesno-infra-smart-ocr-boot.beta.base.infra.linesno.com/api/infra/smart/ocr/generalText" , AipOcrModelEnums.getAllModels())
    )),

    CUSTOM_MODEL("自定义模型", "customModel", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://example.com/api/v1"),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://example.com/api/v1/vector"),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://example.com/api/v1/ranking"),
            new ModelInfo(ModelTypeEnums.SPEECH_RECOGNITION, "https://example.com/api/v1/speech-recognition"),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "https://example.com/api/v1/speech-synthesis"),
            new ModelInfo(ModelTypeEnums.VISION_MODEL, "https://example.com/api/v1/vision"),
            new ModelInfo(ModelTypeEnums.OCR_MODEL, "https://example.com/api/v1/vision"),
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "https://example.com/api/v1/image-generation")
    ));

    private final String displayName;
    private final String code;
    private final List<ModelInfo> supportedModels;

    /**
     * 返回包含所有枚举常量信息的列表，每个元素是一个包含显示名称、公司、代码和支持的模型信息的 Map
     *
     * @return 包含所有枚举常量信息的列表
     */
    public static List<Map<String, Object>> getAllModelProvidersInfo() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (LlmModelProviderEnums provider : values()) {

            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("displayName", provider.getDisplayName());
            infoMap.put("code", provider.getCode());

            // 处理支持的模型信息
            List<Map<String, Object>> modelList = new ArrayList<>();
            for (ModelInfo modelInfo : provider.getSupportedModels()) {
                Map<String, Object> modelMap = new HashMap<>();
                modelMap.put("code", modelInfo.getCode());
                modelMap.put("displayName", modelInfo.getDisplayName());
                modelMap.put("url", modelInfo.getUrl());
                modelMap.put("modelList", modelInfo.getModelList());
                modelMap.put("speechModelList", modelInfo.getSpeechModelList());
                modelList.add(modelMap);
            }

            infoMap.put("supportedModels", modelList);

            result.add(infoMap);
        }
        return result;
    }

    // 定义 ModelInfo 类，用于存储模型类型和对应的 URL
    @AllArgsConstructor
    @Getter
    public static class ModelInfo {
        // 模型类型的代码，例如 "large_language_model"
        private String code;

        // 模型显示名称
        private String displayName;

        // 该模型类型对应的请求地址
        private String url;

        // 大语音模型列表
        private  List<Map<String, String>> modelList ;

        // 语音合成模型列表
        private  List<Map<String, Object>> speechModelList ;

        public ModelInfo(ModelTypeEnums typeEnums, String url) {
            this.code = typeEnums.getCode();
            this.displayName = typeEnums.getDisplayName();
            this.url = url;
        }

        public ModelInfo(ModelTypeEnums typeEnums, String url, List<Map<String, String>> modelList) {
            this.code = typeEnums.getCode();
            this.displayName = typeEnums.getDisplayName();
            this.url = url;
            this.modelList = modelList;
        }

        public ModelInfo(ModelTypeEnums typeEnums, String url, List<Map<String, Object>> speechModels , int type) {
            this.code = typeEnums.getCode();
            this.displayName = typeEnums.getDisplayName();
            this.url = url;
            this.speechModelList = speechModels;
        }
    }

    /**
     * 通过Provider类型和模型名称，获取到模型信息
     */
    public static Map<String, Object> getSpeechModelInfoByTypeAndModelName(String providerType, String modelName) {
        for (LlmModelProviderEnums provider : values()) {
            if (provider.getCode().equals(providerType)) {
                for (ModelInfo modelInfo : provider.getSupportedModels()) {
                    if (modelInfo.getCode().equals(ModelTypeEnums.SPEECH_SYNTHESIS.getCode())) {
                        for (Map<String, Object> speechModel : modelInfo.getSpeechModelList()) {
                            if (speechModel.get("modelName").equals(modelName) ) {
                                log.debug(">>>> " + JSONObject.toJSONString(speechModel.get("modelName")));
                                return speechModel;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
