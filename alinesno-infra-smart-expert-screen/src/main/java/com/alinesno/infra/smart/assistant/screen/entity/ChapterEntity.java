package com.alinesno.infra.smart.assistant.screen.entity;

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
    @Column(name = "chapter_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "章节的名称")
    private String chapterName;

    @Column(name = "chapter_level", type = MySqlTypeConstant.INT, comment = "章节的层级")
    private Integer chapterLevel;

    @Column(name = "chapter_sort", type = MySqlTypeConstant.INT, comment = "章节的排序")
    private Integer chapterSort;

    @Column(name = "chapter_require", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "编写要求")
    private String chapterRequire;

    @Column(name = "chapter_additional_require", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "附加要求")
    private String chapterAdditionalRequire;

    @Column(name = "parent_chapter_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "父章节ID")
    private Long parentChapterId;

    @Column(name = "is_leaf", type = MySqlTypeConstant.TINYINT, length = 1, comment = "是否为叶子节点")
    private Boolean isLeaf;

    @Column(name = "screen_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private long screenId ; // 关联的场景ID

    // 编写人员
    @Column(name = "chapter_editor", type = MySqlTypeConstant.BIGINT, length = 32, comment = "章节编辑人员")
    private long chapterEditor;

    // 子组件
    @TableField(exist = false)
    private List<ChapterEntity> subtitles ;

}
