package com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档审核规则的DTO类
 * 用于文档审核场景中，定义了需要审核的文档所依据的规则信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectKnowledgeDto extends BaseDto {

    private Long projectId;

    // 所属场景ID、所属分组ID、文档名称、文档内容、文档md5编写
    private Long sceneId;

    private Long groupId;

    private String docName;

    // private String docContent;

    private String docMd5;

    // 是否已经向量化setVectorStatus
    private Integer vectorStatus;

}
