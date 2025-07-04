package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto;

import com.alinesno.infra.smart.scene.entity.ArticleManagerEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章管理响应数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleManagerResponseDto extends ArticleManagerEntity {

    private String templateName ; // 所属模板类型

}
