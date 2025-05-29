package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * PPTOutlineDto类用于封装PPT的大纲信息
 * 该类标记了@Slf4j注解，用于日志记录
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class PPTOutlineDto extends BaseDto {

    /**
     * PPT的唯一标识符
     */
    private Long pptId;

    @NotNull(message = "提示词不能为空")
    private String promptText ;

    /**
     * 场景的唯一标识符
     */
    @NotNull(message = "场景不能为空")
    private Long sceneId ;

    /**
     * PPT的大纲内容
     */
    @NotNull(message = "大纲内容不能为空")
    private String outline;

    /**
     * PPT的配置信息
     */
    @NotNull(message = "PPT配置不能为空")
    private PptConfigDto pptConfig;

}

