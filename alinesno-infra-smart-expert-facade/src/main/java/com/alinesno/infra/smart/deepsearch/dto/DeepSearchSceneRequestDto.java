package com.alinesno.infra.smart.deepsearch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 章节提示内容请求数据传输对象
 */
@Data
public class DeepSearchSceneRequestDto {

    /**
     * 场景ID，用于标识特定的场景
     */
    @NotNull(message = "场景ID不能为空")
    private Long sceneId ;

    /**
     * 提示内容，用于描述章节的提示信息
     */
    @NotNull(message = "提示内容不能为空")
    private String promptContent ;

    /**
     * 渠道流ID，用于标识特定的渠道流
     */
    @NotNull(message = "渠道流ID不能为空")
    private String channelStreamId ;

    /**
     * 附件信息，用于描述章节的附件信息
     */
    private String attachments;  // 附件信息

}
