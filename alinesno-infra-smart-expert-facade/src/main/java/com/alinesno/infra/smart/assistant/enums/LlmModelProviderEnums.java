package com.alinesno.infra.smart.assistant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum LlmModelProviderEnums {

    QWEN("阿里百炼(Qwen)", "qwen", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://dashscope.aliyuncs.com"),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://dashscope.aliyuncs.com"),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank/text-rerank"),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "https://dashscope.aliyuncs.com"),
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis")
    )),

    DOUBAO("云雀大模型", "doubao", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://ark.cn-beijing.volces.com/api/v3"),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://ark.cn-beijing.volces.com/api/v3/vector"),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://ark.cn-beijing.volces.com/api/v3/ranking"),
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "https://visual.volcengineapi.com")
    )),

    GITEE("GiteeAI", "gitee", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://ai.gitee.com/v1"),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://ai.gitee.com/v1/vector"),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://ai.gitee.com/v1/ranking"),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "https://ai.gitee.com/v1/speech-synthesis"),
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "https://ai.gitee.com/v1/image-generation")
    )),

    OLLAMA("Ollama", "ollama", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "http://localhost:11434"),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "http://localhost:11434/speech-synthesis"),
            new ModelInfo(ModelTypeEnums.IMAGE_GENERATION, "http://localhost:11434/image-generation")
    )),

    SILICONFLOW("硅基流动", "siliconflow", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://api.siliconflow.cn/v1"),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://api.siliconflow.cn/v1/ranking"),
            new ModelInfo(ModelTypeEnums.SPEECH_RECOGNITION, "https://api.siliconflow.cn/v1/speech-recognition"),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "https://api.siliconflow.cn/v1/audio/speech")
    )),

    QWQ("阿里百炼（QwQ）", "qwq", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://dashscope.aliyuncs.com/compatible-mode/v1")
    )),

    CUSTOM_MODEL("自定义模型", "customModel", List.of(
            new ModelInfo(ModelTypeEnums.LARGE_LANGUAGE_MODEL, "https://example.com/api/v1"),
            new ModelInfo(ModelTypeEnums.VECTOR_MODEL, "https://example.com/api/v1/vector"),
            new ModelInfo(ModelTypeEnums.RE_RANKING_MODEL, "https://example.com/api/v1/ranking"),
            new ModelInfo(ModelTypeEnums.SPEECH_RECOGNITION, "https://example.com/api/v1/speech-recognition"),
            new ModelInfo(ModelTypeEnums.SPEECH_SYNTHESIS, "https://example.com/api/v1/speech-synthesis"),
            new ModelInfo(ModelTypeEnums.VISION_MODEL, "https://example.com/api/v1/vision"),
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
            List<Map<String, String>> modelList = new ArrayList<>();
            for (ModelInfo modelInfo : provider.getSupportedModels()) {
                Map<String, String> modelMap = new HashMap<>();
                modelMap.put("code", modelInfo.getCode());
                modelMap.put("displayName", modelInfo.getDisplayName());
                modelMap.put("url", modelInfo.getUrl());
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

        public ModelInfo(ModelTypeEnums typeEnums, String url) {
            this.code = typeEnums.getCode();
            this.displayName = typeEnums.getDisplayName();
            this.url = url;
        }
    }
}