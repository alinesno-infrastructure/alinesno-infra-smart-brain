package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RewriteConfigDto类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentFormatterRewriteConfigDto extends BaseDto {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;  // 场景ID

    @NotNull(message = "重写配置不能为空")
    private String rewriteConfig;  // 重写配置

    @NotNull(message = "数据范围不能为空")
    private String dataScope ; // 数据范围
}
