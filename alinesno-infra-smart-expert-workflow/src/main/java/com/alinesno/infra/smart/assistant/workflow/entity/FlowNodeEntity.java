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

/**
 * 工作流节点表实体类，记录每个工作流的节点情况信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("flow_nodes")
@TableComment(value = "工作流节点表")
public class FlowNodeEntity extends InfraBaseEntity {

    /**
     * 所属工作流的 ID
     */
    @TableField(value = "flow_id")
    @ColumnType(value = MySqlTypeConstant.BIGINT)
    @ColumnComment("所属工作流的 ID")
    private Long flowId;

    /**
     * 界面工作流步骤节点ID
     */
    @TableField(value = "step_id")
    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("界面工作流步骤节点ID")
    private String stepId;

    /**
     * 节点名称
     */
    @TableField(value = "node_name")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("节点名称")
    private String nodeName;

    /**
     * 节点类型
     */
    @TableField(value = "node_type")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @ColumnComment("节点类型")
    private String nodeType;

    /**
     * 节点在工作流图中的横坐标位置
     */
    @TableField(value = "x")
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("节点在工作流图中的横坐标位置")
    private int x;

    /**
     * 节点在工作流图中的纵坐标位置
     */
    @TableField(value = "y")
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("节点在工作流图中的纵坐标位置")
    private int y;

    /**
     * 节点的属性，采用键值对的形式存储额外信息
     * 这里假设以 JSON 字符串形式存储，实际使用时可根据需求调整
     */
    @TableField(value = "properties")
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("节点的属性，采用键值对的形式存储额外信息")
    private String properties;

    /**
     * 后续节点 ID 列表，以逗号分隔存储
     */
    @TableField(value = "next_node_ids")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("后续节点 ID 列表，以逗号分隔存储")
    private String nextNodeIds;

    /**
     * 前置节点 ID 列表，以逗号分隔存储
     */
    @TableField(value = "prev_node_ids")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 255)
    @ColumnComment("前置节点 ID 列表，以逗号分隔存储")
    private String prevNodeIds;
}