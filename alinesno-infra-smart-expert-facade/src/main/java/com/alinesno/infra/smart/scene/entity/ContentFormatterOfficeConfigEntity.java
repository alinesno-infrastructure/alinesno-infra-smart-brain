package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Office工具套件路径
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("content_formatter_office_config")
public class ContentFormatterOfficeConfigEntity extends InfraBaseEntity {

    // 工具路径、工具名称、工具请求Token
    @TableField
    @Column(name = "tool_path", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "工具路径")
    private String toolPath;

    @TableField
    @Column(name = "tool_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "工具名称")
    private String toolName;

    @TableField
    @Column(name = "request_token", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "工具请求Token")
    private String requestToken;

    // 工具范围
    @TableField
    @Column(name = "data_scope", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "工具范围")
    private String dataScope ;

}