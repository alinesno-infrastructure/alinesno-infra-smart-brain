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
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LongTextSceneDto extends SceneDto {

    /**
     * 章节编辑人员
     */
    private List<IndustryRoleDto> chapterEditors = new ArrayList<>();

    /**
     * 内容编辑人员
     */
    private List<IndustryRoleDto> contentEditors = new ArrayList<>();

    /**
     * 章节树节点信息
     */
    private List<TreeNodeDto> chapterTree = new ArrayList<>();
}
