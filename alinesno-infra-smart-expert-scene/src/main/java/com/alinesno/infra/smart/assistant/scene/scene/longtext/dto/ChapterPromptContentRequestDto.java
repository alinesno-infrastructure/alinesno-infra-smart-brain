package com.alinesno.infra.smart.assistant.scene.scene.longtext.dto;

import lombok.Data;

/**
 * 章节提示内容请求数据传输对象
 */
@Data
public class ChapterPromptContentRequestDto {

    private Long sceneId ;
    private String promptContent ;

}
