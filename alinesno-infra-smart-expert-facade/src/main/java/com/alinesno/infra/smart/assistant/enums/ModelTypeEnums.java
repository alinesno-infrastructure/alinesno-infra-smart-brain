package com.alinesno.infra.smart.assistant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum ModelTypeEnums {

    LARGE_LANGUAGE_MODEL("大语言模型", "large_language_model"),
    VECTOR_MODEL("向量模型", "vector_model"),
    OFFICE_RECOGNITION("文档识别", "office_recognition"),
    RE_RANKING_MODEL("重排模型", "re_ranking_model"),
    SPEECH_RECOGNITION("语音识别", "speech_recognition"),
    SPEECH_SYNTHESIS("语音合成", "speech_synthesis"),
    VISION_MODEL("视觉模型", "vision_model"),
    OCR_MODEL("OCR识别", "ocr_model"),
    IMAGE_GENERATION("图片生成", "image_generation");

    private final String displayName;
    private final String code;

    public static List<Map<String, Object>> getAllModelTypesInfo() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ModelTypeEnums modelType : values()) {
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("displayName", modelType.getDisplayName());
            infoMap.put("code", modelType.getCode());
            result.add(infoMap);
        }
        return result;
    }

    /**
     * 通过code获取到ModelTypeEnums
     */
    public static ModelTypeEnums getByCode(String code) {
        for (ModelTypeEnums modelType : values()) {
            if (modelType.getCode().equals(code)) {
                return modelType;
            }
        }
        return null;
    }

    /**
     * 通过code获取到displayName
     * @param modelType
     * @return
     */
    public static String getDisplayNameByCode(String modelType) {
        for (ModelTypeEnums modelTypeEnum : values()) {
            if (modelTypeEnum.getCode().equals(modelType)) {
                return modelTypeEnum.getDisplayName();
            }
        }
        return "未分类";
    }
}