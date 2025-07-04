package com.alinesno.infra.smart.assistant.scene.common.examPaper.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 创建考试信息的DTO类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamInfoDto extends BaseDto {

    @NotBlank(message = "考试名称不能为空")
    @Size(max = 100, message = "考试名称长度不能超过100个字符")
    private String examName;

    @NotNull(message = "考试开始时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @NotNull(message = "考试结束时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @NotNull(message = "试卷ID不能为空")
    private Long paperId;

    @NotNull(message = "考试类型不能为空")
    @Min(value = 1, message = "考试类型最小值为1")
    @Max(value = 3, message = "考试类型最大值为3")
    private Integer examType;

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

}