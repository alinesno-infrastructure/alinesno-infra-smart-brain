package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 考试配置DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamPagerGeneratorDTO extends BaseDto {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @NotBlank(message = "难度等级不能为空")
    private String difficultyLevel;

    @NotNull(message = "考试结构不能为空")
    @Valid
    private ExamStructureItem examStructureItem;

    @Valid
    private List<Long> attachments;

    @NotEmpty(message = "提示文本不能为空")
    private String promptText;

    @NotBlank(message = "channelStreamId不能为空")
    private String channelStreamId ;

    public MessageTaskInfo toPowerMessageTaskInfo() {
        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setOrgId(this.getOrgId());
        taskInfo.setDepartmentId(this.getDepartmentId());
        taskInfo.setOperatorId(this.getOperatorId());

        return taskInfo ;
    }
}