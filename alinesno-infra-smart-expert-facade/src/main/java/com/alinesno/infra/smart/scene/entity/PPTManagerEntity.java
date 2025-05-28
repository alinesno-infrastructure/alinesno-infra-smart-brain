package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 深度搜索场景实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ppt_manager")
public class PPTManagerEntity extends InfraBaseEntity {

    @TableField("scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField("ppt_generator_scene_id")
    @Column(type = MySqlTypeConstant.BIGINT, length = 32, comment = "PPT场景ID")
    private Long pptGeneratorSceneId ;

    // 标题、描述、封面、排版
    @TableField("title")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "标题")
    private String title;

    @TableField("description")
    @Column(type = MySqlTypeConstant.TEXT, comment = "描述")
    private String description;

    @TableField("cover")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "封面")
    private String cover;

    @TableField("layout")
    @Column(type = MySqlTypeConstant.VARCHAR, length = 255, comment = "排版")
    private String layout;

    // 大纲列表，使用json保存
    @TableField("outline_list")
    @Column(type = MySqlTypeConstant.TEXT, comment = "大纲列表")
    private String outlineList;

}
