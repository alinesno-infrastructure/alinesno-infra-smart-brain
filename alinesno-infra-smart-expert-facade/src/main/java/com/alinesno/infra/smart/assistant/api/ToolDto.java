package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ToolDto extends BaseDto {

    private String icon; // 工具图标
    private String name; // 工具名称
    private String author; // 作者
    private String description; // 描述信息
    private String toolType;
    private String groovyScript;
    private boolean isOnline;
    private Long useCount;
    private Long errorCount;
    private Long successCount;
    private String toolInfo ; // 工具信息

}
