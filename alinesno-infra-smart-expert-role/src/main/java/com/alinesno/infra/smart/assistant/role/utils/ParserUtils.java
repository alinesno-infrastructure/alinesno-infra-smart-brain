package com.alinesno.infra.smart.assistant.role.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

@Slf4j
public class ParserUtils {

    public static String convertYamlToJson(String content) {
        String json = null;

        Yaml yaml = new Yaml();
        try {
            Map<String, Object> data = yaml.load(content);

            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(data);

            log.debug("json = {}" , json);

        } catch (Exception e) {
        }

        return json;
    }
}
