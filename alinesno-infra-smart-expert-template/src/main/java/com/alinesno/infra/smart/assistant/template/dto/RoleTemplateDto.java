package com.alinesno.infra.smart.assistant.template.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.alinesno.infra.smart.assistant.annotation.ToolInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.tools.Tool;
import java.util.List;

@ToString
@Data // 自动生成所有字段的getter和setter方法，以及toString(), equals(), hashCode()方法
@NoArgsConstructor // 自动生成无参构造函数
@AllArgsConstructor // 自动生成全参构造函数
public class RoleTemplateDto {

    @JSONField(name = "id")
    private Long id;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "type")
    private String type;

    @JSONField(name = "desc")
    private String desc;

    @JSONField(name = "abilities")
    private List<String> abilities;

    @JSONField(name = "opening_line")
    private String openingLine;

    @JSONField(name = "tools")
    private List<RoleToolInfo> tools;

}