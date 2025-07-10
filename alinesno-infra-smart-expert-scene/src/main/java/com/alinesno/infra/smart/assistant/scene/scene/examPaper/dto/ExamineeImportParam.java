package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExamineeImportParam extends BaseDto {

    @NotNull(message = "分组ID不能为空")
    private Long groupId;

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @NotNull(message = "数据不能为空")
    private String data;
}