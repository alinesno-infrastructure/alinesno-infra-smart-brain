package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章更新数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleUpdateDto extends BaseDto {

    private Long sceneId;

    private Long articleGeneratorSceneId ;

    // 标题、描述、封面、排版
    private String title;

    private String description;

    // 文章内容
    private String content;

    // 所使用模板
    private String templateId;
}
