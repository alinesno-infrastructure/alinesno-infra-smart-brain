package com.alinesno.infra.smart.assistant.role.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.util.List;

public class YAMLMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    // 将YAML字符串转换为Java对象
    public static <T> T fromYAML(String yaml, Class<T> valueType) throws IOException {
        return objectMapper.readValue(yaml, valueType);
    }

    // 将Java对象转换为YAML字符串
    public static String toYAML(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    // 将YAML字符串转换为List类型的Java对象
    public static <T> List<T> listFromYAML(String yaml, Class<T> elementType) throws IOException {
        return objectMapper.readValue(yaml, objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
    }
}