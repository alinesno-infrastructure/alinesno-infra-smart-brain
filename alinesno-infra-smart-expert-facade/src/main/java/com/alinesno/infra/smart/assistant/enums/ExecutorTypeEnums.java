package com.alinesno.infra.smart.assistant.enums;

import lombok.Getter;

/**
 * 任务执行列表,包括shell/python/sql/maven/http/jar任务
 */
@Getter
public enum ExecutorTypeEnums {

    START_FLOW(0, "开始结点", "startFlow"),
    SHELL_SCRIPT(1, "SHELL脚本", "shell"),
    HTTP_REQUEST(2, "HTTP请求", "http"),
    PYTHON_SCRIPT(3, "PYTHON脚本", "python"),
    SQL_SCRIPT(4, "SQL脚本", "sql"),
    MAVEN_COMMAND(5, "MAVEN命令", "maven"),
    JAR_EXECUTION(6, "ANSIBLE执行", "ansible"),
    K8S_OPERATION(7, "K8S操作", "k8s"),
    GIT_CHECKOUT(8, "Checkout操作", "checkout"),
    NOTICE(9, "发送通知", "notice")
    ;

    private final int type;
    private final String name;
    private final String code;

    ExecutorTypeEnums(int type, String name, String code) {
        this.type = type;
        this.name = name;
        this.code = code;
    }

    public static ExecutorTypeEnums fromType(int type) {
        for (ExecutorTypeEnums scriptType : values()) {
            if (scriptType.getType() == type) {
                return scriptType;
            }
        }
        throw new IllegalArgumentException("未知类型: " + type);
    }

    public static ExecutorTypeEnums fromCode(String code) {
        for (ExecutorTypeEnums scriptType : values()) {
            if (scriptType.getCode().equalsIgnoreCase(code)) {
                return scriptType;
            }
        }
        throw new IllegalArgumentException("未知代码: " + code);
    }

}
