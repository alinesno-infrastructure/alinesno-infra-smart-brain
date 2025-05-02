package com.alinesno.infra.base.search.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("dataset_parse_log")
public class DatasetParseLogEntity extends InfraBaseEntity {

    @TableField("thread_name")
    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("线程名称")
    private String threadName;

    @TableField("start_time")
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("开始时间")
    private Date startTime;

    @TableField("end_time")
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("结束时间")
    private Date endTime;

    @TableField("status")
    @ColumnType(value = MySqlTypeConstant.VARCHAR , length = 64)
    @ColumnComment("状态")
    private String status;

    @TableField("error_message")
    @ColumnType(value = MySqlTypeConstant.TEXT)
    @ColumnComment("错误信息")
    private String errorMessage;
}    