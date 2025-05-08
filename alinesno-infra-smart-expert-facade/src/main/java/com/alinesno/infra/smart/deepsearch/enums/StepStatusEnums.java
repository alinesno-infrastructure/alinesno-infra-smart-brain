package com.alinesno.infra.smart.deepsearch.enums;

import lombok.Getter;

/**
 * 步骤状态枚举类
 */
@Getter
public enum StepStatusEnums {

    SUCCESS("done" , "成功"),
    FAIL("fail" , "失败"),
    RUNNING("doing" , "运行中");

    private final String code;
    private final String desc;

    StepStatusEnums(String code , String desc){
        this.code = code;
        this.desc = desc;
    }

}
