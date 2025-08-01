package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 智能文档配置表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("content_formatter_config")
public class ContentFormatterConfigEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private Long sceneId; // 关联的场景ID

    @TableField
    @Column(name = "content_formatting_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的内容排版场景ID")
    private Long contentFormattingSceneId; // 关联的内容排版场景ID

    @TableField
    @Column(comment = "Rewrite重写配置" , type = MySqlTypeConstant.TEXT)
    private String rewriteConfig ;

    @TableField
    @Column(comment = "排序" , length = 11 , type = MySqlTypeConstant.INT)
    private Integer sort ;

    @TableField
    @Column(name = "data_scope", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "工具范围")
    private String dataScope ;

}