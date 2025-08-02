package com.alinesno.infra.smart.im.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息内容引用实体对象，用于存储每一条消息引用的三方内容
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("message_reference")
public class MessageReferenceEntity extends InfraBaseEntity {

    @TableField
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "消息ID")
    private String messageId ;

    @TableField
    @Column(type = MySqlTypeConstant.INT, length = 11, comment = "排序")
    private int sort ;  // 解析顺序

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR, length = 64, comment = "名称")
    private String name ;   // 文件名称

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR, length = 64, comment = "类型")
    private String type ; // 类型(知识库/链接/记忆/附件)

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR, length = 64, comment = "文本长度")
    private String textLength ; // 文本长度

    @TableField
    @Column(type = MySqlTypeConstant.LONGTEXT, comment = "内容")
    private String content ; // 内容

    @TableField
    @Column(type = MySqlTypeConstant.VARCHAR, length = 64, comment = "来源")
    private String source ;

}
