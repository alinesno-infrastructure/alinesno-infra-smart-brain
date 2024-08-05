package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnType;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName("channel")
public class PluginEntity extends InfraBaseEntity {

    @ColumnComment("渠道名称")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "name")
    private String name;

    @ColumnComment("作者")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 50)
    @TableField(value = "author")
    private String author;

    @ColumnComment("jar名称")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 256)
    @TableField(value = "jar_name")
    private String jarName;

    @ColumnComment("jar本地路径")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 256)
    @TableField(value = "local_path")
    private String localPath;

    @ColumnComment("描述信息")
    @ColumnType(value = MySqlTypeConstant.VARCHAR, length = 128)
    @TableField(value = "description")
    private String description;

}
