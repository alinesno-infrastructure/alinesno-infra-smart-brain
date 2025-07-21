package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.alinesno.infra.common.security.mapper.AESEncryptHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述：MCP配置与其它三方服务配置
 * @author luoxiaodong
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "mcp_config" , autoResultMap = true)
@Table(comment = "MCP配置界面")
public class McpConfigEntity extends InfraBaseEntity {

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR, length = 128, isNull = false, comment = "MCP名称")
    private String mcpName;

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR, length = 256 , isNull = false, comment = "MCP地址")
    private String mcpUrl;

    @TableField(typeHandler = AESEncryptHandler.class)
    @Column(type = MySqlTypeConstant.VARCHAR, length = 128, isNull = false, comment = "MCP密钥")
    private String mcpToken;

}
