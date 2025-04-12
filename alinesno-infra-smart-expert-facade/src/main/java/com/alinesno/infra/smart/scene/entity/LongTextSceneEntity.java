package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 长文本场景实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("long_text_scene")
public class LongTextSceneEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private long sceneId;

    @TableField
    @Column(name = "chapter_prompt_content", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "Prompt内容生成")
    private String chapterPromptContent ;

    @TableField
    @Column(name = "chapter_editor", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "章节编辑人员")
    private String chapterEditor;

    @TableField
    @Column(name = "content_editor", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "内容编辑人员")
    private String contentEditor;

    @TableField
    @Column(name = "gen_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成状态(1生成菜单|0未生成)")
    private Integer genStatus = 0 ;

}
