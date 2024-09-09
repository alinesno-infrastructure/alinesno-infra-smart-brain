package com.alinesno.infra.smart.inference.enums;

import lombok.Getter;

// 级别枚举
@Getter
public enum Level {

    NONE(0),
    PRIMARY_SCHOOL(10), // 小学
    MIDDLE_SCHOOL(20), // 中学
    HIGH_SCHOOL(30), // 高中
    UNIVERSITY(40), // 大学
    GRADUATE_SCHOOL(50), // 研究生院
    PHD(60), // 博士
    PROFESSIONAL(70); // 职业生涯

    private final int requiredExperience;

    Level(int requiredExperience) {
        this.requiredExperience = requiredExperience;
    }
}