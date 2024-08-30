package com.alinesno.infra.base.im.entity;// 用户频道

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

// 消息实体类
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("message")
public class MessageEntity extends InfraBaseEntity {

    @Column(type = MySqlTypeConstant.BIGINT, length = 11, isNull = false, comment = "消息ID")
    private Long messageId;

    @Column(type = MySqlTypeConstant.BIGINT, length = 11, isNull = false, comment = "发送者用户ID")
    private Long senderId;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 256, isNull = false, comment = "接收者用户ID")
    private String receiverId;

    @Column(type = MySqlTypeConstant.BIGINT, length = 11, isNull = false, comment = "所属频道ID")
    private Long channelId;

    @Column(type = MySqlTypeConstant.LONGTEXT, comment = "消息内容")
    private String content;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 20, isNull = true, comment = "聊天类型")
	private String msgType ; // 聊天类型

    @Column(type = MySqlTypeConstant.DATETIME, comment = "发送时间")
    private Date sendTime;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 20, isNull = true, comment = "消息状态")
    private String status;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 20, isNull = true, comment = "角色类型")
    private String roleType;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, isNull = true, comment = "角色图标")
    private String icon ;

    @Column(type = MySqlTypeConstant.LONGTEXT, isNull = true, comment = "发送人名称")
    private String name;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 6, isNull = true, comment = "读取方式")
    private String readerType ; // 读取方式和类型

    @Column(type = MySqlTypeConstant.BIGINT, length = 32, isNull = true, comment = "业务唯一ID")
    private long businessId ; // 生成的唯一业务ID标识

    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, isNull = true, comment = "样式标识")
    private String className ; // 返回的样式标识

}
