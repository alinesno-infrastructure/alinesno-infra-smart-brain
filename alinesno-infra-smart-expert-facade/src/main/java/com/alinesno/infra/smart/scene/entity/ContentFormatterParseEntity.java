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
 * 内容排版规划实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("content_formatter_parse")
public class ContentFormatterParseEntity extends InfraBaseEntity {

    // 内容排版规划的名称、层级、排序等、编写要求、附加要求、父节点ID、是否为叶子节点
    @TableField
    @Column(name = "formatter_name", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "内容排版规划的名称")
    private String formatterName;

    @TableField
    @Column(name = "formatter_level", type = MySqlTypeConstant.INT, comment = "内容排版规划的层级")
    private Integer formatterLevel;

    @TableField
    @Column(name = "formatter_sort", type = MySqlTypeConstant.INT, comment = "内容排版规划的排序")
    private Integer formatterSort;

    @TableField
    @Column(name = "formatter_require", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "排版要求")
    private String formatterRequire;

    @TableField
    @Column(name = "formatter_additional_require", type = MySqlTypeConstant.VARCHAR, length = 256, comment = "附加排版要求")
    private String formatterAdditionalRequire;

    @TableField
    @Column(name = "parent_formatter_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "父内容排版规划ID")
    private Long parentChapterId;

    @TableField
    @Column(name = "is_leaf", type = MySqlTypeConstant.TINYINT, length = 1, comment = "是否为叶子节点")
    private Boolean isLeaf;

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的场景ID")
    private Long sceneId; // 关联的场景ID

    @TableField
    @Column(name = "content_formatting_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "关联的内容排版场景ID")
    private Long contentFormattingSceneId; // 关联的内容排版场景ID

    // 排版人员
    @TableField
    @Column(name = "content_formatter_engineer_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "内容排版规划编辑人员")
    private Long contentFormatterEngineerId;

    @TableField
    @Column(name = "content", type = MySqlTypeConstant.LONGTEXT, comment = "本内容排版规划文本内容")
    private String content;

    // 子组件
    @TableField(exist = false)
    private List<ContentFormatterParseEntity> subtitles;

}