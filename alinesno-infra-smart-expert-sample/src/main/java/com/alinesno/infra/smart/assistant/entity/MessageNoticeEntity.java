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
@TableName("message_notice")
public class MessageNoticeEntity extends InfraBaseEntity {
	/**
	 * 消息ID
	 */
	@ColumnType(MySqlTypeConstant.BIGINT)
	@ColumnComment("消息ID")
	@TableField("message_id")
	private Long messageId;

	/**
	 * 发送者ID
	 */
	@ColumnType(MySqlTypeConstant.BIGINT)
	@ColumnComment("发送者ID")
	@TableField("sender_id")
	private Long senderId;

	/**
	 * 接收者ID
	 */
	@ColumnType(MySqlTypeConstant.BIGINT)
	@ColumnComment("接收者ID")
	@TableField("receiver_id")
	private Long receiverId;

	/**
	 * 消息内容
	 */
	@ColumnType(MySqlTypeConstant.TEXT)
	@ColumnComment("消息内容")
	@TableField("content")
	private String content;

	/**
	 * 发送时间
	 */
	@ColumnType(value = MySqlTypeConstant.DATETIME, length = 18)
	@ColumnComment("发送时间")
	@TableField("send_time")
	private Date sendTime;


}
