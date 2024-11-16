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

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName("plugins")
@TableComment("插件信息")
public class PluginEntity extends InfraBaseEntity {

    // 插件图标
    @ColumnComment("插件图标")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @TableField(value = "icon")
    private String icon;

    @ColumnComment("插件名称")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "name")
    private String name;

    @ColumnComment("作者")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "author")
    private String author;

    @ColumnComment("描述信息")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @TableField(value = "description")
    private String description;

    @ColumnComment("插件类型")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "plugin_type")
    private String pluginType;

    // 插件脚本(groovy)
    @ColumnComment("插件脚本")
    @ColumnType(value = MySqlTypeConstant.LONGTEXT)
    @TableField(value = "groovy_script")
    private String groovyScript;

    // ------>>>>>>>>>>>> 指标信息 ---->>>>>>>>>.
    // 是否上架
    @ColumnComment("是否上架")
    @ColumnType(value = MySqlTypeConstant.TINYINT, length = 1)
    @TableField(value = "is_online")
    private boolean isOnline;

    // 使用数量/成功次数/异常次数
    @ColumnComment("使用数量")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 11)
    @TableField(value = "use_count")
    private Long useCount;

    @ColumnComment("异常数量")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 11)
    @TableField(value = "error_count")
    private Long errorCount;

    @ColumnComment("成功数量")
    @ColumnType(value = MySqlTypeConstant.BIGINT, length = 11)
    @TableField(value = "success_count")
    private Long successCount;



}
