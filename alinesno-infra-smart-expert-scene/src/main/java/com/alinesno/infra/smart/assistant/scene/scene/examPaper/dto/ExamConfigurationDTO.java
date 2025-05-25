package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 考试配置DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamConfigurationDTO extends BaseDto {

    @NotBlank(message = "场景ID不能为空")
    private String sceneId;

    @NotBlank(message = "难度等级不能为空")
    private String difficultyLevel;

    @NotEmpty(message = "考试结构不能为空")
    @Valid
    private List<ExamStructureItem> examStructure;

    @Valid
    private List<String> attachments;

    @NotEmpty(message = "提示文本不能为空")
    private String promptText;

}