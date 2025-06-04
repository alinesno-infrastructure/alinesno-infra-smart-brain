package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章生成管理
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("article_manager")
public class ArticleManagerEntity extends InfraBaseEntity {

    @TableField("scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField("article_generator_scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "PPT场景ID")
    private Long articleGeneratorSceneId ;

    // 标题、描述、封面、排版
    @TableField("title")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "标题")
    private String title;

    @TableField("description")
    @Column(type = MySqlTypeConstant.TEXT, comment = "描述")
    private String description;

    // 文章内容
    @TableField("content")
    @Column(type = MySqlTypeConstant.TEXT, comment = "文章内容")
    private String content;

    // 所使用模板
    @TableField("template_id")
    @Column(type = MySqlTypeConstant.VARCHAR , length = 32, comment = "所使用模板")
    private String templateId;

}
