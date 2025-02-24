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

    DEEDSEEK(
            "DeepSeek",
            "深度求索",
            "deepseek",
            "https://dashscope.aliyuncs.com/compatible-mode/v1",
            createModelList("deepseek-v3", "deepseek-r1")
    ),
    DOUBAO(
            "豆包",
            "字节跳动",
            "doubao",
            "https://ark.cn-beijing.volces.com/api/v3",
            createModelList("ep-20250210101146-9ds66")
    ),
    CHATGPT(
            "ChatGPT",
            "OpenAI公司",
            "chatgpt",
            "https://api.openai.com",
            createModelList("gpt-3.5-turbo", "gpt-4")
    ),
    TONGYI_QIYWEN(
            "通义千问",
            "阿里巴巴",
            "qwen",
            "https://dashscope.aliyuncs.com/compatible-mode/v1",
            createModelList("qwen-turbo", "qwen-plus")
    );

    private final String displayName;
    private final String company;
    private final String code;
    private final String url;
    private final List<Map<String, String>> modelList;

    /**
     * 创建模型列表
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
     * @return 包含所有枚举常量信息的列表
     */
    public static List<Map<String, Object>> getAllModelProvidersInfo() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (LlmModelProviderEnums provider : values()) {
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("displayName", provider.getDisplayName());
            infoMap.put("company", provider.getCompany());
            infoMap.put("code", provider.getCode());
            infoMap.put("url", provider.getUrl());
            infoMap.put("modelList", provider.getModelList());
            result.add(infoMap);
        }
        return result;
    }

}