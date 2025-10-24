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
                基于RAG(Retrieval-Augmented Generation)技术的知识库上传与检索一体化工具。
                该工具专门用于处理用户上传文档后立即进行检索查询的场景，实现上传与查询的无缝衔接。
                主要特点：
                - 专为动态上传的文档集合设计
                - 支持实时索引和即时检索
                - 适用于临时性、会话性的知识库需求
                - 自动处理新上传文档的向量化和索引创建
                使用场景：
                • 用户上传PDF、Word等文档后立即进行问答
                • 临时会议资料的分析和查询
                • 项目周期内的文档快速检索
                • 不需要长期存储的临时知识库需求
                与OutsideRagTool的区别：
                - UploadRagTool：面向动态上传的临时文档，强调实时性和临时性
                - OutsideRagTool：面向预定义的静态知识库，强调稳定性和长期可用性
                """
)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class UploadRagTool extends Tool {

    @ParamInfo(description = "需要查询的自然语言文本内容")
    private String queryText;

    private String collectionIndexName;

    @Setter
    private Llm llm ;

    @Setter
    private EmbeddingOptions embeddingOptions ;

    public UploadRagTool(String queryText, String collectionIndexName) {
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