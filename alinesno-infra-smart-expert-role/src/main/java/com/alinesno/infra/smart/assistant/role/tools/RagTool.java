package com.alinesno.infra.smart.assistant.role.tools;

import cn.hutool.extra.spring.SpringUtil;
import com.alinesno.infra.smart.assistant.annotation.ParamInfo;
import com.alinesno.infra.smart.assistant.annotation.ToolInfo;
import com.alinesno.infra.smart.assistant.plugin.tool.Tool;
import com.alinesno.infra.smart.assistant.role.utils.OutsideDatasetUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 全文检索工具类，实现本地Rag查询工具
 */
@Slf4j
@ToolInfo(
        description = "基于RAG(Retrieval-Augmented Generation)技术的知识库检索工具。" +
                "当需要从指定的知识库集合中查询相关信息时使用此工具。" +
                "该工具能够根据查询文本从预定义的文档集合中检索最相关的信息片段。" +
                "适用于需要参考已存储知识库内容的场景。"
)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class RagTool extends Tool {

    @ParamInfo(description = "需要查询的自然语言文本内容")
    private String queryText;

    private String collectionIndexName;

    public RagTool(String queryText, String collectionIndexName) {
        this.collectionIndexName = collectionIndexName;
        this.queryText = queryText;
    }

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();

        OutsideDatasetUtil outsideUtil = SpringUtil.getBean(OutsideDatasetUtil.class);

        String result = outsideUtil.search(collectionIndexName, queryText, null);
        sb.append(result);

        return sb.toString();
    }
}