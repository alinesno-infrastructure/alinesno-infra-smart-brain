// AbstractWorkflowNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个抽象父类，继承自 WorkflowNode 接口。
 * 它作为所有具体工作流节点类的基类，包含了所有工作流节点可能通用的属性和方法。
 * 由于它是抽象类，不能直接实例化，具体的工作流节点类需要继承该类并实现必要的逻辑。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Data
public abstract class AbstractFlowNode implements FlowNode {

    // 节点唯一标识符，用于唯一标识一个节点
    private String id;

    // 节点名称，用于唯一标识一个节点
    private String name;

    // 节点的x坐标，用于定义节点在界面中的水平位置
    private int x;

    // 节点的y坐标，用于定义节点在界面中的垂直位置
    private int y;

    // 节点的高度，用于定义节点在界面中占据的垂直空间大小
    private int height;

    // 节点类型，用于区分不同功能或属性的节点
    private String type ;

    // 节点属性集合，用于存储节点的额外属性信息，如颜色、大小等
    private Map<String, Object> properties = new HashMap<>();

}