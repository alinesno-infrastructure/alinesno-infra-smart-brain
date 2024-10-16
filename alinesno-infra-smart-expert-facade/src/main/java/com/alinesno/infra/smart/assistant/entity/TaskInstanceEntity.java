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

import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("task_instance")
@TableComment(value = "任务实例表")
public class TaskInstanceEntity extends InfraBaseEntity {

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 64)
    @ColumnComment("任务名称")
    private String name;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("任务类型")
    private String taskType;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("任务状态")
    private int state;

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("流程ID")
    private long processId ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("流程实例ID")
    private long processInstanceId;

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("任务编码")
    private long taskCode;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("任务描述")
    private String description;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("任务所在服务器")
    private String host;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("任务所在服务器地址")
    private String executePath;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("任务日志路径")
    private String logPath;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("失败重试次数")
    private int retryTimes;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("最大失败重试次数")
    private int maxRetryTimes;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("失败重试间隔")
    private int retryInterval;

    @TableField
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("任务参数")
    private String taskParams;

    @TableField(exist = false)
    private Map<String, String> taskParamMap;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("是否为测试任务")
    private int dryRun;

    @TableField
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("开始时间")
    private Date startTime;

    @TableField
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("结束时间")
    private Date endTime;

    @TableField
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("错误信息")
    private String errorMsg ;

}