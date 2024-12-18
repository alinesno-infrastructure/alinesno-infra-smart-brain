package com.alinesno.infra.smart.assistant.api.validate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class ValidJsonValidator implements ConstraintValidator<ValidJson, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 空字符串或null不进行校验
        }

        try {
            JsonNode node = objectMapper.readTree(value);
            if (!node.isObject()) {
                return false; // 不是JSON对象
            }
            // 尝试将JSON对象转换为Map
            Map<String, Object> map = objectMapper.convertValue(node, Map.class);
            return true; // 成功转换为Map
        } catch (Exception e) {
            return false; // 解析失败，不是有效的JSON
        }
    }
}