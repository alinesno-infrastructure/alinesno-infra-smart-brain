package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.annotation.TableComment;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("process_instance")
@TableComment(value = "流程实例")
public class ProcessInstanceEntity extends InfraBaseEntity {

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 32)
    @ColumnComment("流程编码")
    private long processId ;

    // 工作空间
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("工作空间")
    private String workspace;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 64)
    @ColumnComment("流程名称")
    private String name;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("流程描述")
    private String description;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 32)
    @ColumnComment("项目编码")
    private long projectId;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 8)
    @ColumnComment("状态")
    private int state;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("运行次数")
    private int runTimes;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("运行机器")
    private String host;

    @TableField
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("命令参数")
    private String commandParam;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("失败重试次数")
    private int maxTryTimes;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("超时时间")
    private int timeout;

    @TableField
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("全局参数")
    private String globalParams;

    @TableField
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("开始时间")
    private Date startTime;

    @TableField
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("结束时间")
    private Date endTime;

    @TableField
    @ColumnType(value = MySqlTypeConstant.DATETIME)
    @ColumnComment("重试时间")
    private Date restartTime;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT, length = 2)
    @ColumnComment("是否恢复,flag for failover")
    private int recovery;

}