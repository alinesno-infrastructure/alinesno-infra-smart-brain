package com.alinesno.infra.smart.assistant.screen.agent.bean;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WorkerResponseJsonTest {

    @Test
    public void testParseAndExtractData() {
        // 示例 JSON 字符串
        String jsonString = "{\n" +
                "    \"thought\": \"为什么选择这个工具的思考\",\n" +
                "    \"tool_names\": [\"工具名1\"],\n" +
                "    \"args_list\": {\n" +
                "        \"工具名1\": {\n" +
                "            \"question\": \"这是一个问题吗？\",\n" +
                "            \"answer\": \"这是答案。\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        // 将 JSON 字符串解析为 WorkerResponseJson 对象
        WorkerResponseJson workerResponseJson = JSON.parseObject(jsonString, WorkerResponseJson.class);

        // 验证 thought
        assertEquals("为什么选择这个工具的思考", workerResponseJson.getThought());

        // 验证 toolNames
        List<String> toolNames = workerResponseJson.getToolNames();
        assertNotNull(toolNames);
        assertEquals(1, toolNames.size());
        assertEquals("工具名1", toolNames.get(0));

        // 验证工具名1的问题
        String question = workerResponseJson.getQuestion("工具名1");
        assertEquals("这是一个问题吗？", question);

        // 验证工具名1的答案
        String answer = workerResponseJson.getAnswer("工具名1");
        assertEquals("这是答案。", answer);

        // 验证工具名1的所有参数
        Map<String, Object> params = workerResponseJson.getToolParams("工具名1");
        assertNotNull(params);
        assertEquals("这是一个问题吗？", params.get("question"));
        assertEquals("这是答案。", params.get("answer"));
    }
}