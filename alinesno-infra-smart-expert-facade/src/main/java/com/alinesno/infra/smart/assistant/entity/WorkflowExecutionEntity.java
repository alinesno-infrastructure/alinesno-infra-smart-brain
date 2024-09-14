package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 工作流执行记录表实体类
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("workflow_executions") // MyBatis-Plus 注解，指定表名
public class WorkflowExecutionEntity extends BaseEntity {

    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("角色ID")
    @TableField("role_id")
    private long roleId ;  // 角色ID

    @ColumnComment("频道ID")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @TableField("channel_id")
    private long channelId;  // 频道ID

    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("构建次数")
    @TableField("build_number")
    private Integer buildNumber; // 构建次数

    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("节点名称或标识符")
    @TableField("workflow_name")
    private String workflowName; // 节点名称或标识符

    @ColumnComment("节点执行开始时间")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 16)
    @TableField("start_time")
    private long startTime; // 工作流执行开始时间

    @ColumnComment("节点执行结束时间")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 16)
    @TableField("end_time")
    private long endTime; // 工作流执行结束时间

    @ColumnType(value = MySqlTypeConstant.VARCHAR,length = 16)
    @ColumnComment("节点执行状态")
    @TableField("status")
    private String status; // 工作流执行状态

    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("节点执行时间")
    @TableField("usage_time_seconds")
    public String usageTimeSeconds ;

    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("节点执行的日志信息")
    @TableField("log_info")
    private String logInfo; // 节点执行的日志信息

    @TableField("gen_content")
    @ColumnComment("节点生成的内容")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    private String genContent ; // 节点生成的内容

}
