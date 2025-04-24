package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通用智能体场景初始化请求数据传输对象
 */
@Data
public class GeneralAgentInitDto {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId ;

    @NotNull(message = "业务处理专员不能为空")
    private String businessProcessorEngineer;

    @NotNull(message = "业务执行专员不能为空")
    private String businessExecuteEngineer;

    @NotNull(message = "业务展示专员不能为空")
    private String dataViewerEngineer;

}
