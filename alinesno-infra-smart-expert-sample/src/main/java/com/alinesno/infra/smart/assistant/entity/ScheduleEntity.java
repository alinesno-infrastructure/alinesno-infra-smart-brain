package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("schedule")
public class ScheduleEntity extends InfraBaseEntity {
	/**
	 * 日程ID
	 */
	@ColumnType(MySqlTypeConstant.BIGINT)
	@ColumnComment("日程ID")
	@TableField("schedule_id")
	private Long scheduleId;

	/**
	 * 用户ID
	 */
	@ColumnType(MySqlTypeConstant.BIGINT)
	@ColumnComment("用户ID")
	@TableField("user_id")
	private Long userId;

	/**
	 * 日程内容
	 */
	@ColumnType(MySqlTypeConstant.TEXT)
	@ColumnComment("日程内容")
	@TableField("content")
	private String content;

	/**
	 * 提醒时间
	 */
	@ColumnType(value = MySqlTypeConstant.DATETIME, length = 18)
	@ColumnComment("提醒时间")
	@TableField("reminder_time")
	private Date reminderTime;


}
