package com.alinesno.infra.smart.assistant.plugin.tool;

import groovy.lang.GroovyClassLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
public class ToolExecutor {

    public static ToolResult executeGroovyScript(String script, Map<String, Object> params, Map<String, String> secretKey) throws Exception {
       log.trace("执行脚本：{}", script);

        try (GroovyClassLoader loader = new GroovyClassLoader()) {
            Class<?> groovyClass = loader.parseClass(script);

            if (!Tool.class.isAssignableFrom(groovyClass)) {
                throw new IllegalArgumentException("The provided Groovy script does not extend the Tool class.");
            }

            Tool toolInstance = (Tool) groovyClass.getDeclaredConstructor().newInstance();
            toolInstance.setSecretKey(secretKey);

            String toolJson = toolInstance.toJson();
            log.debug("工具参数：{}", toolJson);

            setParams(toolInstance, params);

            Object result = toolInstance.execute();
            boolean isFinished = toolInstance.isFinished();

            ToolResult toolResult = new ToolResult();

            toolResult.setFinished(isFinished);
            toolResult.setOutput(result);

            return toolResult ;
        }
    }

    @SneakyThrows
    public static String getToolInfo(String script) {

        log.trace("执行脚本：{}", script);

        try (GroovyClassLoader loader = new GroovyClassLoader()) {
            Class<?> groovyClass = loader.parseClass(script);

            if (!Tool.class.isAssignableFrom(groovyClass)) {
                throw new IllegalArgumentException("The provided Groovy script does not extend the Tool class.");
            }

            Tool toolInstance = (Tool) groovyClass.getDeclaredConstructor().newInstance();

            String toolJson = toolInstance.toJson();
            log.debug("工具参数：{}", toolJson);

            return toolJson ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setParams(Tool tool, Map<String, Object> params) throws IllegalAccessException {
        Field[] fields = tool.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (params.containsKey(field.getName())) {
                field.setAccessible(true);
                field.set(tool, params.get(field.getName()));
            }
        }
    }
}