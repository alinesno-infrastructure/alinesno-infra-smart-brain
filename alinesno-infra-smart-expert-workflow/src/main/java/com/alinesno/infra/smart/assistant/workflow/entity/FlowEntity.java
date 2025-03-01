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
 * 工作流表实体类，用于定义工作流基础信息和元数据信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("flows")
@TableComment(value = "工作流表")
public class FlowEntity extends InfraBaseEntity {

    @TableField(value = "role_id")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("角色ID")
    private Long roleId;

    @TableField(value = "flow_graph_json")
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("工作流图JSON数据")
    private String flowGraphJson; // 工作流图JSON数据

    @TableField(value = "flow_version")
    @ColumnType(value = MySqlTypeConstant.INT, length = 5)
    private int flowVersion; // 工作流版本(使用最新的版本号，当其它版本状态为不可用)

    @TableField(value = "lock_version")
    @ColumnType(value = MySqlTypeConstant.INT, length = 5)
    @ColumnComment("乐观锁版本号")
    private int lockVersion; // 乐观锁版本号

    @TableField(value = "publish_status")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 20)
    @ColumnComment("发布状态，未发布、已发布")
    private String publishStatus; // 发布状态
}