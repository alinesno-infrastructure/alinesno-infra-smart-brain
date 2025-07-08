package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试批阅数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamMarkingDto extends BaseDto {

    @NotNull(message = "考试ID不能为空")
    private Long examId; // 考试ID

    @NotNull(message = "场景ID不能为空")
    private Long sceneId ; // 场景ID

    @NotNull(message = "考生ID不能为空")
    private Long examineeId; // 考生ID

    @NotNull(message = "试题列表及分数不能为空")
    private JSONArray questionScores ;

    @NotNull(message = "总分不能为空")
    private Long totalScore ;

    @NotNull(message = "考试分数占比不能为空")
    private Long scorePercentage ;
}
