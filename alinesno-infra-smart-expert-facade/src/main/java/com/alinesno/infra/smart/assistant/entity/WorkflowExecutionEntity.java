package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

// 工作流执行记录表实体类
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("workflow_executions") // MyBatis-Plus 注解，指定表名
public class WorkflowExecutionEntity extends BaseEntity {

    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("所属应用")
    @TableField("workflow_id")
    private Long workflowId; // 关联到特定工作流程的ID

    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("构建次数")
    @TableField("build_number")
    private Integer buildNumber; // 构建次数

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("节点名称或标识符")
    @TableField("workflow_name")
    private String workflowName; // 节点名称或标识符

    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("流程执行步骤 ")
    @TableField("step_with_time")
    private String stepWithTime; // 流程执行步骤

    @ColumnComment("节点执行开始时间")
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @TableField("start_time")
    private LocalDateTime startTime; // 工作流执行开始时间

    @ColumnComment("节点执行结束时间")
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @TableField("end_time")
    private LocalDateTime endTime; // 工作流执行结束时间

    @ColumnType(value = MySqlTypeConstant.VARCHAR,length = 16)
    @ColumnComment("节点状态")
    @TableField("workflow_status")
    private String workflowStatus; // 节点状态

    @ColumnType(value = MySqlTypeConstant.VARCHAR,length = 16)
    @ColumnComment("节点执行状态")
    @TableField("status")
    private String status; // 工作流执行状态

    @ColumnType(value = MySqlTypeConstant.DOUBLE)
    @ColumnComment("节点执行时间")
    @TableField("usage_time_seconds")
    public Long usageTimeSeconds ;

    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("节点执行的日志信息")
    @TableField("log_info")
    private String logInfo; // 节点执行的日志信息
}
