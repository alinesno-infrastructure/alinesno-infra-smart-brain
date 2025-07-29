package com.alinesno.infra.smart.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户常用功能记录实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("frequent_agent")
@Table(name = "frequent_agent", comment = "常用功能记录表")
public class FrequentAgentEntity extends InfraBaseEntity {

    @Column(type = MySqlTypeConstant.BIGINT, length = 20,  comment = "用户ID")
    private Long accountId ;

    @Column(type = MySqlTypeConstant.VARCHAR, length = 50,  comment = "智能体")
    private Long agentId;

    // 类型，用于区分角色和场景
    @Column(type = MySqlTypeConstant.VARCHAR, length =  10,  comment = "类型(agent智能体|scene场景)")
    private String type;

    @Column(type = MySqlTypeConstant.INT, length = 11,  defaultValue = "0", comment = "点击/使用次数")
    private Integer clickCount;

}