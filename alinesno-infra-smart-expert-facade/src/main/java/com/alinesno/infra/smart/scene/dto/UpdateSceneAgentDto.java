package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 更新场景的DTO类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateSceneAgentDto extends BaseDto {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @NotNull(message = "场景类型ID不能为空")
    private Long sceneTypeId;

    @NotBlank(message = "场景类型代码不能为空")
    private String sceneTypeCode;

    @Valid
    @NotNull(message = "代理列表不能为空")
    private List<@Valid AgentDto> agents;

    @Data
    public static class AgentDto {
        @NotNull(message = "代理ID不能为空")
        private Long id;

        @NotBlank(message = "代理代码不能为空")
        private String code;

        @NotNull(message = "选择的代理ID不能为空")
        private Long selectAgentId;
    }
}