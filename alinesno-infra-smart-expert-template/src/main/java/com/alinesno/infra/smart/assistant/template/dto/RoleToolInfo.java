package com.alinesno.infra.smart.assistant.template.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class RoleToolInfo {

    private String icon ; // 图标

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "desc")
    private String description;

    @JSONField(name = "code")
    private String code;

    @JSONField(name = "script")
    private String script ;
}