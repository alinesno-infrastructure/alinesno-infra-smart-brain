package com.alinesno.infra.smart.brain.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 生成的任务和内容
 *
 * @author luoxiaodong
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("generate_task")
public class GenerateTaskEntity extends InfraBaseEntity {

    @TableField
    @ColumnType(length=128)
    @ColumnComment("任务描述")
    private String taskDesc ;

    @TableField
	@ColumnType(length=64)
	@ColumnComment("业务ID")
    private String businessId ; // 业务ID

    @TableField
	@ColumnType(length=64)
	@ColumnComment("GPT角色ID")
    private String promptId ; // GPT角色ID

    @TableField
	@ColumnType(value = MySqlTypeConstant.LONGTEXT)
	@ColumnComment("生成内容")
    private String assistantContent ;  // 生成内容

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("重试次数")
    private int retryCount = 0 ;

    @TableField
	@ColumnType(length=1)
	@ColumnComment("任务状态,0排队中/1运行中/2已完成/9失败")
    private int taskStatus ;  // 0排队中/1运行中/2已完成/9失败

}
