package com.alinesno.infra.smart.assistant.scene.scene.longtext.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 长文本场景数据传输对象
 * 继承自SceneDto，用于处理长文本场景下的数据传输
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LongTextSceneDto extends SceneDto {

    /**
     * 章节提示内容
     * 用于存储章节的提示或概述信息
     */
    private String chapterPromptContent ;

    /**
     * 章节编辑器
     * 用于标识或存储编辑章节所使用的编辑器信息
     */
    private String chapterEditor;

    /**
     * 内容编辑器
     * 用于标识或存储编辑内容所使用的编辑器信息
     */
    private String contentEditor;

    /**
     * 生成状态
     * 用于表示内容的生成状态，初始值为0
     */
    private Integer genStatus = 0 ;

    /**
     * 章节编辑人员
     * 存储负责编辑章节的人员信息，每个人员信息是一个IndustryRoleDto对象
     */
    private List<IndustryRoleDto> chapterEditors = new ArrayList<>();

    /**
     * 内容编辑人员
     * 存储负责编辑内容的人员信息，每个人员信息是一个IndustryRoleDto对象
     */
    private List<IndustryRoleDto> contentEditors = new ArrayList<>();

    /**
     * 章节树节点信息
     * 存储章节的树状结构信息，每个节点信息是一个TreeNodeDto对象
     */
    private List<TreeNodeDto> chapterTree = new ArrayList<>();
}
