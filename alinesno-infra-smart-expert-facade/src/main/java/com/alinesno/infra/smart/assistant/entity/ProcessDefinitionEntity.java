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
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@TableName("process_definition")
@TableComment(value = "流程定义表")
public class ProcessDefinitionEntity extends InfraBaseEntity {

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("流程名称")
    private String name ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("流程描述")
    private String description ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.BIGINT)
    @ColumnComment("环境ID")
    private long envId ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 2)
    @ColumnComment("数据采集模板")
    private String dataCollectionTemplate ;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("项目编码")
    private long projectId;

    @TableField(exist = false)
    private String projectName;  // 项目名称

    @TableField
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @ColumnComment("全局参数")
    private String globalParams;

    @TableField(exist = false)
    private Map<String, String> globalParamMap;

    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("超时时间")
    private int timeout;

    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("调度策略")
    private String scheduleCron ;

    // 是否上线
    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("是否上线")
    private boolean online ;

    // 运行次数
    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("运行次数")
    private long runCount ;

    // 成功次数
    @TableField
    @ColumnType(value = MySqlTypeConstant.INT)
    @ColumnComment("成功次数")
    private long successCount ;

    // 开始时间
    @TableField
    @ColumnType(value = MySqlTypeConstant.DATETIME, length = 20)
    @ColumnComment("开始时间")
    private Date startTime ;

    // 结束时间
    @TableField
    @ColumnType(value = MySqlTypeConstant.DATETIME, length = 20)
    @ColumnComment("结束时间")
    private Date endTime ;

    // 监控通知邮件
    @TableField
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @ColumnComment("监控通知邮件")
    private String monitorEmail ;

}