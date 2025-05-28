package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AIPPTRequestDto {

    @NotBlank(message = "所属PPT不能为空")
    private Long pptId ;

    @NotBlank(message = "内容不能为空")
    private String content;   // 输入内容

    @NotBlank(message = "渠道ID不能为空")
    private String channelStreamId ;

    @NotNull(message = "是否流模式不能为空")
    private Boolean stream;   // 是否流模式（前端传递的 stream: true 会自动映射为 boolean）
}