package com.alinesno.infra.smart.assistant.role.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class FutureDebugger {
    public static void printDependents(CompletableFuture<?> future) {
        try {
            Field stackField = CompletableFuture.class.getDeclaredField("stack");
            stackField.setAccessible(true);
            Object stack = stackField.get(future);

            if (stack != null) {
                // 获取回调函数（UniAccept/UniApply 等）
                Field fnField = stack.getClass().getDeclaredField("fn");
                fnField.setAccessible(true);
                Object callback = fnField.get(stack);

                System.out.println("Callback object: " + callback);
                System.out.println("Callback class: " + callback.getClass().getName());

                // 尝试解析 Lambda 表达式或方法引用
                if (callback.getClass().isSynthetic()) {
                    System.out.println("This is a Lambda or method reference");
                    // 通过反射获取 Lambda 的静态方法（JDK 实现依赖）
                    try {
                        Method lambdaMethod = callback.getClass().getDeclaredMethod("get$Lambda");
                        lambdaMethod.setAccessible(true);
                        Object lambdaInfo = lambdaMethod.invoke(callback);
                        System.out.println("Lambda info: " + lambdaInfo);
                    } catch (NoSuchMethodException e) {
                        System.out.println("Cannot extract Lambda details (JDK version may affect this)");
                    }
                }

                // 如果是方法引用，尝试获取目标类和方法
                if (callback instanceof java.lang.invoke.SerializedLambda) {
                    java.lang.invoke.SerializedLambda serializedLambda =
                            (java.lang.invoke.SerializedLambda) callback;
                    System.out.println("Method reference class: " + serializedLambda.getImplClass());
                    System.out.println("Method reference method: " + serializedLambda.getImplMethodName());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to inspect callback: " + e.getMessage());
        }
    }
}