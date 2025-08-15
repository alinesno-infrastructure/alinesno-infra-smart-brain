package com.alinesno.infra.smart.scene.dto;

import com.alibaba.fastjson.JSONObject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新场景配置数据传输对象
 */
@Data
public class UpdateSceneConfigDto {

    @NotNull(message = "场景ID不能为空")
    private String sceneId;

    @NotNull(message = "配置数据不能为空")
    private JSONObject config;

}
