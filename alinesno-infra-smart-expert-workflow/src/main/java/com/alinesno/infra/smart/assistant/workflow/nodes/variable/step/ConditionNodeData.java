package com.alinesno.infra.smart.assistant.workflow.nodes.variable.step;

import com.alinesno.infra.smart.assistant.workflow.nodes.variable.NodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * ConditionNodeData
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@Data
public class ConditionNodeData extends NodeData {

    private String color;
    private Boolean showNode;
    private String stepName;
    private String icon;
    private Integer width;
    private Config config;
    private Integer height;
    private List<BranchCondition> branchConditionList;

    @Data
    public static class Config {
        private List<Field> fields;
    }
    
    @Data
    public static class Field {
        private String label;
        private String value;
    }
    
    @Data
    public static class BranchCondition {
        private String condition;
        private String id;
        private String type;
        private List<Condition> conditions;
        private Integer height;
        private Integer index;
    }
    
    @Data
    public static class Condition {
        private String compare;
        private List<Object> field; // 可能是数组，包含UUID和字符串
        private String value;
    }

}