package com.alinesno.infra.smart.assistant.scene.common.examPaper.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试信息分页参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamInfoPage extends BaseDto {

    @NotNull(message = "pageNum不能为空")
    private Integer pageNum;

    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    @NotNull(message = "sceneId不能为空")
    private Long sceneId;

    private Integer examStatus ;

}
