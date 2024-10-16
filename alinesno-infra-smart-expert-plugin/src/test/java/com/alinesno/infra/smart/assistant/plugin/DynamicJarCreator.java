//package com.alinesno.infra.smart.assistant.plugin;
//
//import groovy.lang.GroovyClassLoader;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//public class DynamicJarCreator {
//
//    public static void main(String[] args) throws IOException {
//        // 创建一个Groovy类加载器
//        GroovyClassLoader gcl = new GroovyClassLoader();
//
//        // 定义一个简单的Groovy脚本
//        String script = "class HelloWorld {\n" +
//                "    static String sayHello(String name) {\n" +
//                "        return 'Hello, World! ' + name;\n" +
//                "    }\n" +
//                "}\n";
//
//        Class<?> groovyClass = gcl.parseClass(script);
//
//        // 获取类名
//        String className = groovyClass.getName();
//
//        // 从GroovyClassLoader中获取编译后的字节码
//        byte[] classBytes = gcl.getTreeBootsClassBytes(className);
//
//        // 指定保存的文件路径
//        String filePath = "E:\\HelloWorld.class";
//
//        // 将字节码写入到指定文件
//        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//            fos.write(groovyClass.getBytes());
//            System.out.println("Groovy class has been saved to: " + filePath);
//        }
//    }
//
//}