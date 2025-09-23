package com.alinesno.infra.smart.assistant.role.utils;

import lombok.Data;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.util.Arrays;

public class GroovySandbox {

    /**
     * 创建安全的编译器配置
     */
    public static CompilerConfiguration createSecureCompilerConfiguration() {
        CompilerConfiguration config = new CompilerConfiguration();

        // 1. 安全AST定制器
        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setClosuresAllowed(true);
        secure.setMethodDefinitionAllowed(true);
        secure.setIndirectImportCheckEnabled(true);

        // 允许的语句类型(白名单方式更安全)
        secure.setStatementsWhitelist(Arrays.asList(
                ExpressionStatement.class,
                BlockStatement.class,
                ReturnStatement.class,
                IfStatement.class,
                ForStatement.class,
                WhileStatement.class,
                TryCatchStatement.class
        ));

        // 禁止的危险操作
        secure.setReceiversBlackList(Arrays.asList(
                "java.lang.System",
                "java.lang.Runtime",
                "java.lang.Process",
                "java.lang.Thread",
                "java.lang.ClassLoader",
                "java.lang.reflect"
        ));

        // 2. 导入定制器
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStarImports("java.util");
        imports.addStarImports("com.agentsflex");

        config.addCompilationCustomizers(secure, imports);
        return config;
    }

    /**
     * 内存限制检查器
     */
    @Data
    public static class MemoryChecker {
        private final long maxMemory;
        private final long startMemory;

        public MemoryChecker(long maxMemory) {
            this.maxMemory = maxMemory;
            this.startMemory = getCurrentMemory();
        }

        public void check() {
            long used = getCurrentMemory() - startMemory;
            if (used > maxMemory) {
                throw new SecurityException("Memory limit exceeded: " + used + " > " + maxMemory);
            }
        }

        private long getCurrentMemory() {
            Runtime runtime = Runtime.getRuntime();
            return runtime.totalMemory() - runtime.freeMemory();
        }
    }

}
