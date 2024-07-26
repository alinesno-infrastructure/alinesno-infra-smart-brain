package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

// 节点执行记录表实体类
@TableName("workflow_node_executions") // MyBatis-Plus 注解，指定表名
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkflowNodeExecutionEntity extends BaseEntity {

    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("所属应用")
    @TableField("workflow_execution_id")
    private Long workflowExecutionId; // 关联到工作流程执行的ID

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("节点名称或标识符")
    @TableField("node_name")
    private String nodeName; // 节点名称或标识符

    @ColumnComment("节点执行开始时间")
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private LocalDateTime startTime; // 节点执行开始时间

    @ColumnComment("节点执行结束时间")
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private LocalDateTime endTime; // 节点执行结束时间

    @ColumnType(value = MySqlTypeConstant.BIGINT)
    @ColumnComment("执行时间")
    @TableField("time_spent")
    private Long timeSpent ;

    @ColumnType(value = MySqlTypeConstant.VARCHAR,length = 16)
    @ColumnComment("节点执行状态")
    @TableField("status")
    private String status; // 节点执行状态

    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("节点执行的日志信息")
    @TableField("log_info")
    private String logInfo; // 节点执行的日志信息

}
