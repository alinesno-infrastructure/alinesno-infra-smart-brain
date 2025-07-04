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
 * 章节目录
 */
@EqualsAndHashCode(callSuper = true)
@TableName("chapters")
@Data
public class ChapterEntity extends InfraBaseEntity {

    // 章节的名称、层级、排序等、编写要求、附加要求、父节点ID、是否为叶子节点
    @TableField
    @Column(name = "chapter_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "章节的名称")
    private String chapterName;

    @TableField
    @Column(name = "chapter_level", type = MySqlTypeConstant.INT, comment = "章节的层级")
    private Integer chapterLevel;

    @TableField
    @Column(name = "chapter_sort", type = MySqlTypeConstant.INT, comment = "章节的排序")
    private Integer chapterSort;

    @TableField
    @Column(name = "chapter_require", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "编写要求")
    private String chapterRequire;

    @TableField
    @Column(name = "chapter_additional_require", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "附加要求")
    private String chapterAdditionalRequire;

    @TableField
    @Column(name = "parent_chapter_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "父章节ID")
    private Long parentChapterId;

    @TableField
    @Column(name = "is_leaf", type = MySqlTypeConstant.TINYINT, length = 1, comment = "是否为叶子节点")
    private Boolean isLeaf;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private Long sceneId ; // 关联的场景ID

    @TableField
    @Column(name = "long_text_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的长文本场景ID")
    private Long longTextSceneId; // 关联的长文本场景ID

    @TableField
    @Column(name = "task_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的任务ID")
    private Long taskId ; // 关联的任务ID

    // 编写人员
    @TableField
    @Column(name = "chapter_editor", type = MySqlTypeConstant.BIGINT, length = 32, comment = "章节编辑人员")
    private Long chapterEditor;

    @TableField
    @Column(name = "content", type = MySqlTypeConstant.LONGTEXT, comment = "本章节文本内容")
    private String content ;

    // 子组件
    @TableField(exist = false)
    private List<ChapterEntity> subtitles ;

}
