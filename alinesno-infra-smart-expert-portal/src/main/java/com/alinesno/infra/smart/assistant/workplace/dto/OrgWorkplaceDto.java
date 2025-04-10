package com.alinesno.infra.smart.assistant.workplace.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织工作站数据传输对象
 * 用于在组织工作站相关操作中传递数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrgWorkplaceDto extends BaseDto {

    /**
     * 工作站ID
     * 唯一标识一个工作站
     */
    @NotNull(message = "工作站ID不能为空")
    private Long workplaceId;

    /**
     * 工作站名称
     * 描述工作站的名称信息
     */
    @NotBlank(message = "工作站名称不能为空")
    private String workplaceName;

}
