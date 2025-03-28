package com.alinesno.infra.smart.scene.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScreenAgent {
    private final String name;
    private final String code;
    private final String description;
}