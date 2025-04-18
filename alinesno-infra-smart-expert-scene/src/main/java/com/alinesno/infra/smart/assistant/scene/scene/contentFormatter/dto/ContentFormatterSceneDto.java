package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容排版场景 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentFormatterSceneDto extends SceneDto {

    /**
     * 场景 ID
     */
    private long sceneId;

    /**
     * 用于存储与内容排版相关的提示内容，可作为引导内容排版流程或操作的文本信息
     */
    private String contentPromptContent;

    /**
     * 记录负责模板提取的工程师姓名或标识，方便追溯责任人
     */
    private String templateExtractorEngineer;

    /**
     * 记录负责模板提取的工程师信息，方便追溯责任人
     */
    private List<IndustryRoleDto> templateExtractorEngineers;

    /**
     * 记录负责内容审核的工程师姓名或标识，方便追溯责任人
     */
    private String contentReviewerEngineer;

    /**
     * 记录负责内容审核的工程师信息，方便追溯责任人
     */
    private List<IndustryRoleDto> contentReviewerEngineers;

    /**
     * 生成状态(1 生成菜单|0 未生成)，默认未生成
     */
    private Integer genStatus = 0;

    /**
     * 章节树节点信息
     * 存储章节的树状结构信息，每个节点信息是一个TreeNodeDto对象
     */
    private List<TreeNodeDto> chapterTree = new ArrayList<>();
}    