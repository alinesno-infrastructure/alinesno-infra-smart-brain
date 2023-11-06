package com.alinesno.infra.smart.brain.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

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
	@ColumnType(length=255)
	@ColumnComment("业务ID")
    private String businessId ; // 业务ID

    @TableField
	@ColumnType(length=255)
	@ColumnComment("GPT角色ID")
    private String promptId ; // GPT角色ID

    @TableField
	@ColumnType(length=255)
	@ColumnComment("GPT角色设定")
    private String systemContent ;  // GPT角色设定

    @TableField
	@ColumnType(length=255)
	@ColumnComment("GPT用户信息")
    private String userContent ;  // GPT用户信息

    @TableField
	@ColumnType(length=255)
	@ColumnComment("生成内容")
    private String assistantContent ;  // 生成内容

    @TableField
	@ColumnType(length=10)
	@ColumnComment("任务状态,0排队中/1运行中/2已完成/9失败")
    private int taskStatus ;  // 0排队中/1运行中/2已完成/9失败

    public void copyFrom(GenerateTaskEntity task) {
    }
}
