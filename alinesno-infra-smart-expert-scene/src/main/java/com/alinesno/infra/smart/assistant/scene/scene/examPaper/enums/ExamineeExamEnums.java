package com.alinesno.infra.smart.assistant.scene.scene.examPaper.enums;

import lombok.Getter;

/**
 * 考试人员试卷状态(未开始考试/考试中/考试结束/阅卷中/阅卷结束/已结束/已取消)
 */
@Getter
public enum ExamineeExamEnums {

    NOT_STARTED("not_started", "未开始考试"),
    EXAMINATION("examination", "考试中"),
    EXAMINATION_END("examination_end", "考试结束"),
    REVIEW("review", "阅卷中"),
    REVIEW_END("review_end", "阅卷结束"),
    ENDED("ended", "已结束"),
    CANCELED("canceled", "已取消");


    private final String code ;
    private final String name ;

    ExamineeExamEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // 通过code获取到对应的枚举
    public static ExamineeExamEnums getByCode(String code) {
        for (ExamineeExamEnums value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
