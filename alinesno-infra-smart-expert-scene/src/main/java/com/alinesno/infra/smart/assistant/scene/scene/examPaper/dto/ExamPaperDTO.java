package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ExamPaperDTO {

    @NotBlank(message = "场景ID不能为空")
    private String sceneId;

    @NotBlank(message = "频道流ID不能为空")
    private String channelStreamId;

    @NotBlank(message = "试卷类型不能为空")
    private String pagerType;

    @NotBlank(message = "试卷名称不能为空")
    private String pagerName;

    @NotNull(message = "问题列表不能为null")
    private List<QuestionDTO> questionList;

}