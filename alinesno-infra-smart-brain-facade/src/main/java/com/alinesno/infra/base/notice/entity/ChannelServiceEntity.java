package com.alinesno.infra.base.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;

/**
 * 渠道服务类
 * @author luoxiaodong
 * @since 1.0.0
 */
@TableName("channel_service") // 指定数据库表名
public class ChannelServiceEntity extends InfraBaseEntity {

    // 渠道名称
    @TableField("channel_name")
    private String channelName;

    // 渠道代码
    @TableField("channel_code")
    private String channelCode;

    // 渠道状态
    @TableField("channel_status")
    private Integer channelStatus;

    // 渠道访问量
    @TableField("channel_access_count")
    private Integer channelAccessCount;

    // 异常量
    @TableField("exception_count")
    private Integer exceptionCount;

    // 总请求量
    @TableField("total_request_count")
    private Integer totalRequestCount;

    // 省略了构造函数、getter 和 setter 方法

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
