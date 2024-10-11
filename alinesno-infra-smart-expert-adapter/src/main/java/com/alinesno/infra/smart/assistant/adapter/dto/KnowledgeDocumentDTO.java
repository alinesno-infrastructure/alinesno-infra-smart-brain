package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 知识文档数据传输对象，用于表示和传输知识文档的相关信息
 */
@ToString
@NoArgsConstructor
@Data
public class KnowledgeDocumentDTO implements Serializable {

    /**
     * 文档的唯一标识符
     */
    private long id;

    /**
     * 文档的添加时间，用于记录文档在系统中的创建时间
     */
    private String addTime;

    /**
     * 组织标识符，用于区分不同的组织或部门
     */
    private long orgId;

    /**
     * 数据集标识符，用于关联文档所属的数据集
     */
    private String datasetId;

    /**
     * 文档名称，用于标识文档
     */
    private String documentName;

    /**
     * 文档描述，用于简要说明文档的内容或用途
     */
    private String documentDesc;

    /**
     * 文档数量，用于表示文档包含的文件数量
     */
    private int documentCount;
}
