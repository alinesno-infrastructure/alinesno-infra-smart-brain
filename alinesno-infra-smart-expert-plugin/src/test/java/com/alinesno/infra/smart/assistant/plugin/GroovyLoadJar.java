package com.alinesno.infra.smart.assistant.plugin;

import groovy.lang.GroovyShell;

public class GroovyLoadJar {
    public static void main(String[] args) {
        String groovyScript = """
              import groovy.lang.GroovyClassLoader
                                      
              // 创建GroovyClassLoader
              def gcl = new GroovyClassLoader()
              
              // 加载已经存在的HelloWorld.class文件
              Class<?> helloWorldClass = gcl.parseClass(new File('HelloWorld.class'))
              
              // 创建HelloWorld类的实例
              def instance = helloWorldClass.newInstance()
              
              // 调用sayHello方法
              instance.sayHello()
              
              // 关闭GroovyClassLoader
              gcl.close()
              """ ;

        // 创建 GroovyShell 实例
        GroovyShell shell = new GroovyShell() ;

        // 执行 Groovy 脚本
        shell.evaluate(groovyScript) ;

    }
}
