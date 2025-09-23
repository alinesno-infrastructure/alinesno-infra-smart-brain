package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * KnowledgeSearchNodeData 类用于存储知识搜索节点的数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeSearchNodeData extends NodeData {

    private List<DatasetId> datasetIdList;
    private DatasetSetting datasetSetting;
    private List<String> questionReference;

    /**
     * 内部类，映射datasetIdList中的对象
     */
    @Data
    public static class DatasetId {
        private String icon;
        private String name;
        private Long id;
    }

    /**
     * 从datasetList里面获取到List<Long> id列表
     */
    public List<Long> getDatasetIdList() {
        return datasetIdList.stream().map(DatasetId::getId).toList();
    }


    /**
     * 内部类，映射datasetSetting对象
     */
    @Data
    public static class DatasetSetting {
        private String searchType;
        private int topK;
        private int quoteLimit;
        private boolean reorderResults;
        private String minRelevance;
    }
}
