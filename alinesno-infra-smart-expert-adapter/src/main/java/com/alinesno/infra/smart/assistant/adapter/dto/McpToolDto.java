package com.alinesno.infra.smart.assistant.adapter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
public class McpToolDto extends BaseDto {

    // 工具图标
    private String icon;

    // 工具名称
    private String name;

    // 作者
    private String author;

    // 描述信息
    private String description;

    // 工具类型
    private String toolType;

    // 工具脚本(groovy)
    private String groovyScript;

    // 使用数量/成功次数/异常次数
    private Long useCount;

    // 异常数量
    private Long errorCount;

    // 成功数量
    private Long successCount;

    // 工具信息
    private String toolInfo ;

    // 工具全名
    private String toolFullName;

    // 来源ID
    private Long fromId;

}
