package com.alinesno.infra.smart.assistant.role.tools;

import cn.hutool.extra.spring.SpringUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.embedding.EmbeddingOptions;
import com.alinesno.infra.smart.assistant.annotation.ParamInfo;
import com.alinesno.infra.smart.assistant.annotation.ToolInfo;
import com.alinesno.infra.smart.assistant.plugin.tool.Tool;
import com.alinesno.infra.smart.assistant.role.utils.OutsideDatasetUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 全文检索工具类，实现本地Rag查询工具
 */
@Slf4j
@ToolInfo(
        description = """
                基于RAG(Retrieval-Augmented Generation)技术的预定义知识库检索工具。
                该工具专门用于从已建立、长期维护的知识库集合中检索相关信息。
                主要特点：
                - 面向预定义、静态的知识库集合
                - 检索稳定可靠、长期可用的知识内容
                - 适用于企业标准文档、历史资料等固定知识源
                - 不需要每次上传文档，直接查询已有知识库
                使用场景：
                • 查询企业规章制度、操作手册等标准文档
                • 检索历史项目文档、技术资料库
                • 从已建立的产品文档库中获取信息
                • 需要长期维护和版本控制的知识内容查询
                与UploadRagTool的区别：
                - OutsideRagTool：面向预定义的静态知识库，强调稳定性和长期可用性
                - UploadRagTool：面向动态上传的临时文档，强调实时性和临时性
                典型使用方式：
                "请在公司知识库中查找关于请假制度的规定"
                "从技术文档库中检索Spring Boot配置方法"
                "在产品手册中查询安装步骤说明"
                """
)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class OutsideRagTool extends Tool {

    @ParamInfo(description = "需要查询的自然语言文本内容")
    private String queryText;

    private String collectionIndexName;

    @Setter
    private Llm llm ;

    @Setter
    private EmbeddingOptions embeddingOptions ;

    public OutsideRagTool(String queryText, String collectionIndexName) {
        this.collectionIndexName = collectionIndexName;
        this.queryText = queryText;
    }

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();

        OutsideDatasetUtil outsideUtil = SpringUtil.getBean(OutsideDatasetUtil.class);
        outsideUtil.setLlm(llm);
        outsideUtil.setEmbeddingOptions(embeddingOptions);

        log.debug("查询的数据集:collectionIndexName = {}" , collectionIndexName);

        String result = outsideUtil.search(collectionIndexName, queryText, null);
        sb.append(result);

        log.debug("查询结果:{}" , result);

        return sb.toString();
    }
}