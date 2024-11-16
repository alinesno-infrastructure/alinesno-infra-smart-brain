package com.alinesno.infra.smart.assistant.role.tools;

public class ToolExecutor {

//    public static String executeTool(String toolName, Map<String, Object> argsList) throws Exception {
//        Class<?> toolClass = Class.forName(toolName);
//        Constructor<?> constructor = findMatchingConstructor(toolClass, argsList);
//
//        if (constructor == null) {
//            throw new IllegalArgumentException("No matching constructor found for the provided arguments.");
//        }
//
//        List<Object> constructorArgs = new ArrayList<>();
//        for (Parameter parameter : constructor.getParameters()) {
//            String paramName = parameter.getName();
//            Object paramValue = argsList.get(paramName);
//            if (paramValue == null) {
//                throw new IllegalArgumentException("Missing required parameter: " + paramName);
//            }
//            constructorArgs.add(paramValue);
//        }
//
//        Tool tool = (Tool) constructor.newInstance(constructorArgs.toArray());
//        return tool.execute();
//    }
//
//    private static Constructor<?> findMatchingConstructor(Class<?> toolClass, Map<String, Object> argsList) {
//        for (Constructor<?> constructor : toolClass.getConstructors()) {
//            if (isMatchingConstructor(constructor, argsList)) {
//                return constructor;
//            }
//        }
//        return null;
//    }
//
//    private static boolean isMatchingConstructor(Constructor<?> constructor, Map<String, Object> argsList) {
//        Parameter[] parameters = constructor.getParameters();
//        if (parameters.length != argsList.size()) {
//            return false;
//        }
//
//        for (Parameter parameter : parameters) {
//            String paramName = parameter.getName();
//            Object paramValue = argsList.get(paramName);
//            if (paramValue == null || !parameter.getType().isInstance(paramValue)) {
//                return false;
//            }
//        }
//
//        return true;
//    }

}