package com.alinesno.infra.smart.scene.dto;

import lombok.Data;

import java.util.List;

/**
 * 表示树结构节点的数据传输对象
 */
@Data
public class TreeNodeDto {
    /**
     * 节点的唯一标识符
     */
    private Long id;

    /**
     * 节点的标签，用于显示节点的名称
     */
    private String label;

    /**
     * 节点的描述，提供节点的详细信息
     */
    private String description;

    /**
     * 节点编辑人员
     */
    private Long chapterEditor ;

    /**
     * 子节点编辑图标
     */
    private String chapterEditorAvatar ;

    /**
     * 子节点的排序顺序
     */
    private Integer sortOrder;

    /**
     * 子节点列表，表示当前节点下的子节点
     */
    private List<TreeNodeDto> children;
}
