package com.alinesno.infra.smart.deepsearch.dto;

import lombok.Data;

/**
 * 章节提示内容请求数据传输对象
 */
@Data
public class DeepSearchSceneRequestDto {

    /**
     * 场景ID，用于标识特定的场景
     */
    private Long sceneId ;

    /**
     * 提示内容，用于描述章节的提示信息
     */
    private String promptContent ;

}
