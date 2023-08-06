package com.alinesno.infra.base.notice.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 消息发送历史
 * @author luoxiaodong
 * @since 1.0.0
 */
@TableName("notice_send_history") // 指定数据库表名
public class SendHistoryEntity extends InfraBaseEntity {
}
