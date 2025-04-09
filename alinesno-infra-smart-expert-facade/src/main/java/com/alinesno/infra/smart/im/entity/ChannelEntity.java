package com.alinesno.infra.smart.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 频道实体类
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("channel")
public class ChannelEntity extends InfraBaseEntity {

    @TableField("channel_desc")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 256, isNull = false, comment = "频道Desc")
	private String channelDesc ;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 128, isNull = false, comment = "频道Icon")
	private String icon ;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 64, isNull = false, comment = "频道ID")
    private String channelId;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 50, isNull = false, comment = "频道名")
    private String channelName;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 10, isNull = false, comment = "频道类型")
    private String channelType ;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, isNull = true , comment = "频道进入key")
    private String channelKey ;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 32, isNull = true , comment = "知识库ID")
    private String knowledgeId ;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 128, isNull = true , comment = "知识库类型，以|号进行分割")
    private String knowledgeType;

}