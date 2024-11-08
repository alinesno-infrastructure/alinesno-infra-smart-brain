package com.alinesno.infra.smart.assistant.screen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;


/**
 * ChapterGenFormDto类用于表示章节生成表单的数据传输对象
 * 它封装了与章节相关的数据，以便在系统中传递和处理
 */
@ToString
@Data
public class ChapterGenFormDto {

    /**
     * 场景ID，用于标识章节所属的场景
     */
    @Min(value = 1, message = "场景ID不能小于0")
    private long screenId;

    /**
     * 章节标题，用于展示章节的名称
     */
    @NotBlank(message = "章节标题不能为空")
    private String chapterTitle;

    /**
     * 章节描述，用于提供章节的详细信息或概要
     */
    private String chapterDescription;

    /**
     * 章节ID，用于唯一标识一个章节
     */
    @Min(value = 1, message = "章节ID不能小于0")
    private long chapterId;

}
