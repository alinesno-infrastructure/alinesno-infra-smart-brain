package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 内容排版规划 DTO
 * 用于在不同层之间传输内容排版规划相关的数据，不包含数据库映射相关注解。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentFormatterParseDto extends InfraBaseEntity {

    /**
     * 内容排版规划的名称
     */
    private String formatterName;

    /**
     * 内容排版规划的层级
     */
    private Integer formatterLevel;

    /**
     * 内容排版规划的排序
     */
    private Integer formatterSort;

    /**
     * 排版要求
     */
    private String formatterRequire;

    /**
     * 附加排版要求
     */
    private String formatterAdditionalRequire;

    /**
     * 父内容排版规划 ID
     */
    private Long parentChapterId;

    /**
     * 是否为叶子节点
     */
    private Boolean isLeaf;

    /**
     * 关联的场景 ID
     */
    private Long sceneId;

    /**
     * 关联的内容排版场景 ID
     */
    private Long contentFormattingSceneId;

    /**
     * 内容排版规划编辑人员 ID
     */
    private Long contentFormatterEngineerId;

    /**
     * 本内容排版规划文本内容
     */
    private String content;

    /**
     * 子组件列表
     */
    private List<ContentFormatterParseDto> subtitles;
}    