package com.alinesno.infra.smart.assistant.screen.core.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LongTextDto类用于处理长文本数据传输
 * 它继承自BaseDto，具备基本的数据传输对象的属性和方法
 * 主要用于处理与长文本相关的数据，如文章内容等
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LongTextDto extends BaseDto {

    /**
     * 文本内容
     * 存储长文本的主要内容，如文章的正文部分
     */
    private String content;

    /**
     * 章节ID
     * 表示文本所属的章节，用于分章节管理长文本
     */
    private Long chapterId;

    /**
     * 编辑者ID
     * 表示最后编辑该文本的用户，用于记录编辑责任者
     */
    private Long editor;

}
