package com.alinesno.infra.smart.im.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
    public class WorkflowProcessRequestDTO {

        @Min(value = 1 , message = "角色ID不能为空")
        private long roleId ; // 角色ID

        @Min(value = 1 , message = "频道ID不能为空")
        private long channelId ;

        @NotBlank(message = "要修改的内容不能为空")
        private String modifyContent; // 要修改的内容

        @NotBlank(message = "操作类型不能为空")
        private String action; // 操作类型：扩写、重写、润色

        @NotBlank(message = "调整要求不能为空")
        private String requirement;  // 调整要求

    }