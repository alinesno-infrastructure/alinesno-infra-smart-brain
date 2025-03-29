package com.alinesno.infra.smart.scene.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScreenAgent {
    // 唯一标识符，改为 long 类型且不少于 8 位
    private final long id;
    private final String name;
    private final String code;
    private final String description;
}