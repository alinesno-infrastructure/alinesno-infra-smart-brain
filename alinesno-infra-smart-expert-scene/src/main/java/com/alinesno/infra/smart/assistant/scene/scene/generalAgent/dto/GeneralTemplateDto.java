package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto;

import lombok.Data;

/**
 * 通用智能体模板数据传输对象
 */
@Data
public class GeneralTemplateDto {

    private Long id ;   // 主键ID
    private String icon ;   // 图标
    private String name ;   // 名称
    private String description;   // 描述

}
