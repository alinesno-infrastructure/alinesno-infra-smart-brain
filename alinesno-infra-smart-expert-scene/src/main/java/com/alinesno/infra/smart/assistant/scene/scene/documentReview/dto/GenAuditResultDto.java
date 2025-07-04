package com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 进行审核
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GenAuditResultDto extends BaseDto {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId ;

    @NotNull(message = "任务ID不能为空")
    private Long taskId ;

    @NotNull(message = "渠道流ID不能为空")
    private Long channelStreamId ;

    @NotNull(message = "规则ID不能为空")
    private Long ruleId ;

    @NotNull(message = "审核清单ID不能为空")
    private Long auditId ;

    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}
