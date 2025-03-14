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

    QWEN("千问(Qwen)", "qwen", "https://dashscope.aliyuncs.com/compatible-mode/v1"),
    OPENAI("ChatGPT", "openai", "https://api.openai.com"),
    DOUBAO("豆包", "doubao", "https://ark.cn-beijing.volces.com/api/v3"),
    GITEE("GiteeAI", "gitee", "https://ai.gitee.com"),
    OLLAMA("Ollama", "ollama", "http://localhost:11434"),
    DEEPSEEK("DeepSeek", "deepseek", "https://dashscope.aliyuncs.com/compatible-mode/v1"),
    QWQ("千问（QwQ）", "qwq", "https://dashscope.aliyuncs.com/compatible-mode/v1");

    private final String displayName;
    private final String code;
    private final String url;

    /**
     * 创建模型列表
     *
     * @param modelNames 模型名称数组
     * @return 模型列表
     */
    private static List<Map<String, String>> createModelList(String... modelNames) {
        List<Map<String, String>> list = new ArrayList<>();
        for (String modelName : modelNames) {
            Map<String, String> modelMap = new HashMap<>();
            modelMap.put("modelName", modelName);
            list.add(modelMap);
        }
        return list;
    }

    /**
     * 返回包含所有枚举常量信息的列表，每个元素是一个包含显示名称、公司、代码、url 和模型列表的 Map
     *
     * @return 包含所有枚举常量信息的列表
     */
    public static List<Map<String, Object>> getAllModelProvidersInfo() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (LlmModelProviderEnums provider : values()) {
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("displayName", provider.getDisplayName());
            infoMap.put("code", provider.getCode());
            infoMap.put("url", provider.getUrl());
            result.add(infoMap);
        }
        return result;
    }

}