package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息通知实体信息，用于保存相关IM的消息通知，便于后期二次使用
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("notice")
public class NoticeEntity extends InfraBaseEntity {

    @ColumnType(MySqlTypeConstant.LONGTEXT)
    @ColumnComment("任务名称")
    @TableField
    private String taskName ; // 任务名称

    @ColumnType(length = 32)
    @ColumnComment("业务标识")
    @TableField
    private String businessId ; // 业务标识

    @ColumnType(length = 32)
    @ColumnComment("使用时间")
    @TableField
    private String usageTime ; // 使用时间

    @ColumnType(length = 6)
    @ColumnComment("任务状态")
    @TableField
    private String taskStatus ; // 任务状态

    @ColumnType(length = 32)
    @ColumnComment("完成时间")
    @TableField
    private String finishTime ; // 完成时间


    @ColumnType(length = 16)
    @ColumnComment("任务状态")
    @TableField
    private String env ;

    @ColumnType(length = 32)
    @ColumnComment("执行人")
    @TableField
    private String operator ; // 执行人

    @ColumnType(length = 256)
    @ColumnComment("审批链接")
    @TableField
    private String applyLink ; // 审批时间

    // 机器人相关信息
    @ColumnType(length = 64)
    @ColumnComment("机器人ID")
    @TableField
    private String chatbotUserId ; // 机器人ID

    @ColumnType(length = 64)
    @ColumnComment("发起人ID")
    @TableField
    private String sender ; // 发起人，用于完成之后@通知对方

    @ColumnType(length = 32)
    @ColumnComment("发起人昵称")
    @TableField
    private String senderNick ; // 发起人昵称

}
