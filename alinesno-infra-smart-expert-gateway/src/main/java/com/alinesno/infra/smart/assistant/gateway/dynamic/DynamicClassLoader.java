package com.alinesno.infra.smart.assistant.gateway.dynamic;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义动态类加载器
 */
public class DynamicClassLoader extends URLClassLoader {

    Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    public DynamicClassLoader(Map<String, byte[]> classBytes) {
        super(new URL[0], DynamicClassLoader.class.getClassLoader());
        this.classBytes.putAll(classBytes);
    }

    /**
     * 对外提供的工具方法，加载指定的java源码，得到Class对象
     * @param javaSrc java源码
     * @return
     */
    public static Class<?> load(String javaSrc) throws ClassNotFoundException {
        /**
         * 先试用动态编译工具，编译java源码，得到类的全限定名和class字节码的字节数组信息
         */
        Map<String, byte[]> bytecode = DynamicCompiler.compile(javaSrc);
        if(bytecode != null) {
            /**
             * 传入动态类加载器
             */
            DynamicClassLoader classLoader = new DynamicClassLoader(bytecode);
            /**
             * 加载得到Class对象
             */
            return classLoader.loadClass(bytecode.keySet().iterator().next());
        } else {
            throw new ClassNotFoundException("can not found class");
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buf = classBytes.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        classBytes.remove(name);
        return defineClass(name, buf, 0, buf.length);
    }

}