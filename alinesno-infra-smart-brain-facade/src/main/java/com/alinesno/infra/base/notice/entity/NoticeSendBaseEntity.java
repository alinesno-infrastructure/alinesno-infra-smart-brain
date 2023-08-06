package com.alinesno.infra.base.notice.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDateTime;

/**
 * 通知的基类
 * @author luoxiaodong
 * @since 1.0.0
 */
public class NoticeSendBaseEntity extends InfraBaseEntity {

    @TableField("status")
    private String status; // 通知状态

    @TableField("content")
    private String content; // 通知内容

    @TableField("template_id")
    private String templateId; // 模板ID

    @TableField("send_time")
    private LocalDateTime sendTime; // 发送时间

    @TableField("exception_time")
    private LocalDateTime exceptionTime; // 异常时间

    @TableField("recipient")
    private String recipient; // 接收人

    // 构造函数、Getter和Setter方法等省略

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
