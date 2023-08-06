package com.alinesno.infra.base.notice.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 发送配置类
 * @author luoxiaodong
 * @since 1.0.0
 */
@TableName("notice_send_settings") // 指定数据库表名
public class SendSettingsEntity extends InfraBaseEntity {
}
