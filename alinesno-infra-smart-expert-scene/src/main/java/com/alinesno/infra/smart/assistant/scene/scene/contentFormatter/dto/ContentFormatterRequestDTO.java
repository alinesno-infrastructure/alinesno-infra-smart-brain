package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContentFormatterRequestDTO extends BaseDto {

    @NotBlank(message = "reviewListOption不能为空")
    private String reviewListOption;

    @NotEmpty(message = "文档类型不能为空")
    private List<String> documentType;

    @NotBlank(message = "内容不能为空")
    private String contentPromptContent ;

    @NotEmpty(message = "业务场景不能为空")
    private List<String> businessScenario;

    @NotBlank(message = "模板不能为空")
    private String templateId;

    @NotNull(message = "场景不能为空")
    private Long sceneId;

    @NotNull(message = "角色不能为空")
    private Long roleId ;

    @NotNull(message = "流频道不能为空")
    private Long channelStreamId;

    private String requirement ;   // 内容生成要求

}    