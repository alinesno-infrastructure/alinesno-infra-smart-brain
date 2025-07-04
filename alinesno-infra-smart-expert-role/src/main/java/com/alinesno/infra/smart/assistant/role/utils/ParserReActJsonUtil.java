package com.alinesno.infra.smart.assistant.role.utils;

import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ParserReActJsonUtil类
 */
public class ParserReActJsonUtil {

    /**
     * 解析JSON
     * @param jsonContent
     * @return
     */
    public static WorkerResponseJson parseJSON(String jsonContent) {
        WorkerResponseJson reactResponse;

        try{
           reactResponse = JSON.parseObject(jsonContent, WorkerResponseJson.class);
        }catch(Exception e){
            ObjectMapper mapper = new ObjectMapper();
            // 启用多种宽松解析特性
            mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
            mapper.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
            mapper.configure(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature(), true);

            try {
                reactResponse = mapper.readValue(jsonContent, WorkerResponseJson.class);
            } catch (Exception e1) {
                System.err.println("解析失败: " + e.getMessage());
                // 尝试更彻底的清洗方案
                try {
                    String sanitized = sanitizeJson(jsonContent);
                    reactResponse = mapper.readValue(sanitized, WorkerResponseJson.class);
                } catch (Exception ex) {
                    throw new RuntimeException("角色解析命令异常: " + e1.getMessage());
                }
            }
        }

        return reactResponse ;
    }


    private static String sanitizeJson(String json) {
        // 1. 处理LaTeX公式中的特殊字符
        String sanitized = json.replace("\\(", "\\\\(")
                .replace("\\)", "\\\\)");

        // 2. 处理其他特殊转义
        sanitized = sanitized.replace("\\n", "\\\\n")
                .replace("\\t", "\\\\t");

        // 3. 确保字符串引号正确
        sanitized = sanitized.replace("\"", "\\\"");

        return sanitized;
    }
}
