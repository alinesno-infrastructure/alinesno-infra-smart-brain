package com.alinesno.infra.smart.assistant.screen.core.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChapterDto extends BaseDto {

    private long screenId ; // 关联的场景ID

    // 章节的名称、层级、排序等、编写要求、附加要求、父节点ID、是否为叶子节点
    private String chapterName;

    private Integer chapterLevel;

    private Integer chapterSort;

    private String chapterRequire;

    private String chapterAdditionalRequire;

    private Long parentChapterId;

    private Boolean isLeaf;

    // 编写人员
    private long chapterEditor;

    // 子组件
    private List<ChapterDto> subtitles ;

}
