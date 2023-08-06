package com.alinesno.infra.base.notice.entity;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * 统计基类
 * @author luoxiaodong
 * @since 1.0.0
 */
@TableName("notice_summary") // 指定数据库表名
public class NoticeSummaryEntity extends InfraBaseEntity {

    // 总发送数量
    @TableField("total_send_count")
    private Integer totalSendCount;

    // 成功发送数量
    @TableField("success_send_count")
    private Integer successSendCount;

    // 发送失败数量
    @TableField("failure_send_count")
    private Integer failureSendCount;

    // 待发送数量
    @TableField("pending_send_count")
    private Integer pendingSendCount;

    // 异常数量
    @TableField("exception_count")
    private Integer exceptionCount;

    // 最后发送时间
    @TableField("last_send_time")
    private Date lastSendTime;

    // 邮件发送数量
    @TableField("email_count")
    private Integer emailCount;

    // 短信发送数量
    @TableField("sms_count")
    private Integer smsCount;

    // 推送发送数量
    @TableField("push_count")
    private Integer pushCount;

    // 微信发送数量
    @TableField("wechat_count")
    private Integer wechatCount;

    // 总的渠道发送数量
    @TableField("total_channel_count")
    private Integer totalChannelCount;

    // 省略了构造函数、getter 和 setter 方法

    public Integer getTotalSendCount() {
        return totalSendCount;
    }

    public void setTotalSendCount(Integer totalSendCount) {
        this.totalSendCount = totalSendCount;
    }
}
