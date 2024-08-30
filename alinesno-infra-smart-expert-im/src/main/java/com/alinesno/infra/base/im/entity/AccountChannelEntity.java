package com.alinesno.infra.base.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("account_channel")
public class AccountChannelEntity extends InfraBaseEntity {

	@Column(type = MySqlTypeConstant.BIGINT, length = 11, isNull = false, comment = "用户ID")
	private Long accountId ;

	@Column(type = MySqlTypeConstant.BIGINT, length = 11, isNull = false, comment = "频道ID")
	private Long channelId ;

}