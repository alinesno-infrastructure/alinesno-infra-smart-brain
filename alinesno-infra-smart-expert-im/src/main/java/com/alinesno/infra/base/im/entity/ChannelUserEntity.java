package com.alinesno.infra.base.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 频道包含的用户，即当前的用户类型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("channel_account")
public class ChannelUserEntity extends InfraBaseEntity {

    @Column(type = MySqlTypeConstant.BIGINT, length = 11, isNull = false, comment = "用户ID")
    private Long accountId ;

    @Column(type = MySqlTypeConstant.BIGINT, length = 11, isNull = false, comment = "频道ID")
    private Long channelId ;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 6, isNull = false, comment = "类型(agent代理|user用户)")
    private String accountType ; // 类型(agent代理|user用户)

}
