package com.alinesno.infra.smart.assistant.workflow.parse;

import com.alinesno.infra.smart.assistant.workflow.dto.FlowNodeDto;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 节点打印工具类，用于打印 FlowNodeDto 节点及其后续节点信息，并输出层级
 */
@Slf4j
public class NodePrinter {

    /**
     * 从类型为 "start" 的节点开始，按链表形式打印 FlowNodeDto 节点及其后续节点信息，并输出层级
     * @param nodes 包含 FlowNodeDto 节点的列表
     */
    public static void printNodesAsLinkedList(List<FlowNodeDto> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        // 找到类型为 "start" 的起始节点
        FlowNodeDto startNode = null;
        for (FlowNodeDto node : nodes) {
            if ("start".equals(node.getType())) {
                startNode = node;
                break;
            }
        }

        if (startNode != null) {
            bfsPrintNodes(startNode);
        }
    }

    /**
     * 使用广度优先遍历打印节点及其后续节点信息，并输出层级
     * @param startNode 起始节点
     */
    private static void bfsPrintNodes(FlowNodeDto startNode) {
        Queue<NodeLevelPair> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        int count = 1;

        queue.offer(new NodeLevelPair(startNode, 1));
        visited.add(startNode.getId());

        while (!queue.isEmpty()) {
            NodeLevelPair currentPair = queue.poll();
            FlowNodeDto currentNode = currentPair.node;
            int level = currentPair.level;

            log.debug("node--{}--level--{}--->>>>>>>>start", count, level);
            log.debug("Processing node stepName = {} , type = {} , id = {}", currentNode.getStepName(), currentNode.getType(), currentNode.getId());
            log.debug("node--{}--level--{}--->>>>>>>>end", count, level);
            count++;

            for (FlowNodeDto nextNode : currentNode.getNextNodes()) {
                if (!visited.contains(nextNode.getId())) {
                    queue.offer(new NodeLevelPair(nextNode, level + 1));
                    visited.add(nextNode.getId());
                }
            }
        }
    }

    /**
     * 内部类，用于存储节点和对应的层级
     */
    private static class NodeLevelPair {
        FlowNodeDto node;
        int level;

        NodeLevelPair(FlowNodeDto node, int level) {
            this.node = node;
            this.level = level;
        }
    }
}