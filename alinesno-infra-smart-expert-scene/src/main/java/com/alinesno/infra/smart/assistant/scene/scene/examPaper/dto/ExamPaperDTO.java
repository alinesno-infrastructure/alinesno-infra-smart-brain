package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExamPaperDTO extends BaseDto {

    @NotBlank(message = "场景ID不能为空")
    private Long sceneId;

    @NotBlank(message = "频道流ID不能为空")
    private String channelStreamId;

    @NotBlank(message = "试卷类型不能为空")
    private String pagerType;

    @NotBlank(message = "试卷名称不能为空")
    private String pagerName;

    @NotBlank(message = "试卷描述不能为空")
    private String pagerDesc;

    @NotNull(message = "问题列表不能为null")
    private List<QuestionDTO> questionList;

    @NotNull(message = "难度不能为null")
    private String difficulty;

}