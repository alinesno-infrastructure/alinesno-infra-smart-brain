package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.alinesno.infra.smart.assistant.enums.MessageStatus;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表示消息系统中使用的消息实体类。
 * 此类封装了有关消息的信息，包括内容、状态、创建时间、更新时间、优先级和序列号。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("message_queue")
@TableComment("消息队列")
public class MessageQueueEntity extends InfraBaseEntity {

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR , length = 32, comment = "业务标识ID")
    private String businessId ;

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR , length = 10, comment = "消息状态（例如：待发送、已发送、发送失败）")
    private String status = MessageStatus.PENDING.getValue() ;          // 消息状态（例如：待发送、已发送、发送失败）

    @TableField
    @Column(type = MySqlTypeConstant.INT, comment = "消息优先级")
    private Integer priority;       // 消息优先级

    @TableField
    @Column(type = MySqlTypeConstant.INT, comment = "消息在队列中的序列号")
    private Integer sequence;       // 消息在队列中的序列号

    @TableField
    @Column(type = MySqlTypeConstant.BIGINT , comment = "AgentId")
    private long agentId ; // AgentId

    @TableField
    @Column(type = MySqlTypeConstant.BIGINT , comment = "所属频道ID")
    private long channelId ; // channelId

    @TableField
    @Column(type = MySqlTypeConstant.LONGTEXT  , comment = "消息内容(使用的是Map-JSON格式化)")
    private String content;   // 消息内容(使用的是Map-JSON格式化)

    @TableField
    @Column(type = MySqlTypeConstant.LONGTEXT  , comment = "生成的内容")
    private String assistantContent ;  // 生成的内容

    // 生成的信息内容(这里结合查看服务)
    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR, length = 8  , comment = "链接类型")
    private String linkType ; // 链接类型

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR , length = 512, comment = "链接地址")
    private String linkPath ; // 链接连接地址
}
