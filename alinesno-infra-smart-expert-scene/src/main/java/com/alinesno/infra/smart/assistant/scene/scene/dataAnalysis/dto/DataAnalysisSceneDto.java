package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据分析场景 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataAnalysisSceneDto extends SceneDto {

    private long sceneId;

    private String analysisPromptContent;

    private String textParserEngineer;
    private List<IndustryRoleDto> textParserEngineers;

    private String dataMinerEngineer;
    private List<IndustryRoleDto> dataMinerEngineers;

    private Integer genStatus;

    /**
     * 章节树节点信息
     * 存储章节的树状结构信息，每个节点信息是一个TreeNodeDto对象
     */
    private List<TreeNodeDto> chapterTree = new ArrayList<>();
}    