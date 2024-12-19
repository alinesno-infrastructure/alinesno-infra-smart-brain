package com.alinesno.infra.smart.assistant.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.alinesno.infra.common.security.mapper.AESEncryptHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

// 频道实体类
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "secrets" , autoResultMap = true)
public class SecretEntity extends InfraBaseEntity {

    // 密钥名称还有加密的值
    @TableField(value= "secret_name")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 56, isNull = true , comment = "密钥名称")
    private String secretName;

    @TableField(value= "use_count")
    @Column(type = MySqlTypeConstant.INT, isNull = true , comment = "使用次数")
    private int useCount = 0 ;

    // 密钥描述
    @TableField(value= "secret_desc")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 128, isNull = true , comment = "密钥描述")
    private String secretDesc;

    @TableField(value= "secret_value", typeHandler = AESEncryptHandler.class)
    @Column(type = MySqlTypeConstant.VARCHAR, length = 512, isNull = true , comment = "加密的值")
    private String secretValue;

}