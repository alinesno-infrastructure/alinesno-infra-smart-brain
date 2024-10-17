package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("task_definition")
@TableComment(value = "任务定义表")
public class TaskDefinitionEntity extends InfraBaseEntity {

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 64)
    @ColumnComment("任务编码")
    private String code ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("流程编码")
    private long roleId ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("任务名称")
    private String name;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("任务描述")
    private String description;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("项目编码")
    private long projectId ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("任务类型")
    private String taskType;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("环境编码")
    private long environmentId ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.LONGTEXT, length = 128)
    @ColumnComment("任务参数")
    private String taskParams;

    @TableField
    @ColumnType(value = MySqlTypeConstant.LONGTEXT, length = 128)
    @ColumnComment("任务属性")
    private String attr ;

    @TableField(exist = false)
    private Map<String, String> taskParamMap;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("失败重试次数")
    private int failRetryTimes;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("失败重试间隔")
    private int failRetryInterval;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("超时时间")
    private int timeout;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("延迟时间")
    private int delayTime;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 512)
    @ColumnComment("资源ID")
    private String resourceId;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("任务排序")
    private int orderNum;

    // 异常是否继续执行(跳过|中断)
    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("异常是否继续执行(跳过|中断)")
    private boolean continueIgnore = true;

}