package com.alinesno.infra.smart.assistant.scene.scene.documentReader.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 文档审核初始化数据传输对象
 */
@Data
public class DocReaderInitDto {

    @Min(value = 1 , message = "场景ID不能为空")
    private long sceneId ;

    @Min(value = 1 , message = "文档内容分析专员")
    private long summaryAgentEngineer; // 分析智能工程师

    @Min(value = 1 , message = "文档案例查询专员")
    private long caseQueryAgentEngineer; // 逻辑审核工程师
}
