package com.alinesno.infra.smart.assistant.role.tools;

import groovy.lang.GroovyClassLoader;

import java.lang.reflect.Field;
import java.util.Map;

public class ToolExecutor {

    public static Object executeGroovyScript(String script, Map<String, Object> params) throws Exception {
        GroovyClassLoader loader = new GroovyClassLoader();
        Class<?> groovyClass = loader.parseClass(script);

        if (!Tool.class.isAssignableFrom(groovyClass)) {
            throw new IllegalArgumentException("The provided Groovy script does not extend the Tool class.");
        }

        Tool toolInstance = (Tool) groovyClass.getDeclaredConstructor().newInstance();
        setParams(toolInstance, params);
        return toolInstance.execute();
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