package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 场景开场白
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneGreetingQuestionDto extends BaseDto {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId ;

    @NotNull(message = "开场白问题不能为空")
    private List<@NotNull String> greetingQuestion ;

}
