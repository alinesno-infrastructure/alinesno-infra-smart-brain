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
 * 工作流节点执行表实体类，记录每个节点执行的情况
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("flow_node_executions")
@TableComment(value = "工作流节点执行表")
public class FlowNodeExecutionEntity extends InfraBaseEntity {

    /**
     * 所属工作流执行实例的 ID
     */
    @TableField(value = "flow_execution_id")
    @ColumnType(value = MySqlTypeConstant.BIGINT)
    @ColumnComment("所属工作流执行实例的 ID")
    private Long flowExecutionId;

    /**
     * 节点 ID
     */
    @TableField(value = "node_id")
    @ColumnType(value = MySqlTypeConstant.BIGINT)
    @ColumnComment("节点 ID")
    private Long nodeId;

    /**
     * 界面工作流步骤节点ID
     */
    @TableField(value = "step_id")
    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("界面工作流步骤节点ID")
    private String stepId;

    /**
     * 节点类型
     */
    @TableField(value = "node_type")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("节点类型")
    private String nodeType;

    /**
     * 节点的属性，采用键值对的形式存储额外信息
     * 这里假设以 JSON 字符串形式存储，实际使用时可根据需求调整
     */
    @TableField(value = "properties")
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("节点的属性，采用键值对的形式存储额外信息")
    private String properties;

    /**
     * 执行状态
     */
    @TableField(value = "execution_status")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("执行状态")
    private String executionStatus;

    /**
     * 执行的序号
     */
    @TableField(value = "execution_order")
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("执行的序号")
    private Integer executionOrder;

    /**
     * 执行的图形深度
     */
    @TableField(value = "execution_depth")
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("执行的图形深度")
    private Integer executionDepth;

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