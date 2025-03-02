package com.alinesno.infra.smart.assistant.workflow.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 工作流执行表实体类，记录每个工作流当前执行的实例情况
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("flow_executions")
@TableComment(value = "工作流执行表")
public class FlowExecutionEntity extends InfraBaseEntity {

    /**
     * 所属工作流的 ID
     */
    @TableField(value = "flow_id")
    @ColumnType(value = MySqlTypeConstant.BIGINT)
    @ColumnComment("所属工作流的 ID")
    private Long flowId;

    @TableField(value = "role_id")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("角色ID")
    private Long roleId;

    @TableField(value = "flow_graph_json")
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("工作流图JSON数据")
    private String flowGraphJson; // 工作流图JSON数据

    /**
     * 执行状态
     */
    @TableField(value = "execution_status")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("执行状态")
    private String executionStatus;

    /**
     * 开始时间
     */
    @TableField(value = "execute_time")
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("开始执行时间")
    private Date executeTime;

    /**
     * 结束时间
     */
    @TableField(value = "finish_time")
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("结束时间")
    private Date finishTime;
}