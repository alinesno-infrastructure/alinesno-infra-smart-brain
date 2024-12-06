//package com.alinesno.infra.smart.assistant.gateway.dynamic;
//
//import javax.tools.JavaCompiler;
//import javax.tools.JavaFileObject;
//import javax.tools.StandardJavaFileManager;
//import javax.tools.ToolProvider;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * 动态编译java源码类
// */
//public class DynamicCompiler {
//
//    /**
//     * 编译指定java源代码
//     * @param javaSrc java源代码
//     * @return 返回类的全限定名和编译后的class字节码字节数组的映射
//     */
//    public static Map<String, byte[]> compile(String javaSrc) {
//        Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
//        Matcher matcher = pattern.matcher(javaSrc);
//        if (matcher.find()) {
//            return compile(matcher.group(1) + ".java", javaSrc);
//        }
//        return null;
//    }
//
//    /**
//     * 编译指定java源代码
//     * @param javaName java文件名
//     * @param javaSrc java源码内容
//     * @return 返回类的全限定名和编译后的class字节码字节数组的映射
//     */
//    public static Map<String, byte[]> compile(String javaName, String javaSrc) {
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
//        try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
//            JavaFileObject javaFileObject = manager.makeStringSource(javaName, javaSrc);
//            JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, null, null, Collections.singletonList(javaFileObject));
//            if (task.call()) {
//                return manager.getClassBytes();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//}